#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <pcap.h>
#include <signal.h>
#include <sys/ioctl.h>
#include <linux/if.h>

#include "prism.h"
#include "rssiList.h"

#define PORT 1236
#define RESPONSE_PORT 1237

#define BUFFER_SIZE 500

// http://192.168.1.177:8080/ServerPositionning/MobileCalibrationListener?address=38:e7:d8:b0:15:08&x=11&y=11&map=1

struct ieee80211_header
{
	u_short frame_control;
	u_short frame_duration;
	u_char recipient[6];
	u_char source_addr[6];
	u_char address3[6];
	u_short sequence_control;
	u_char address4[6];
};


pcap_t* handle = NULL;
struct prism_header *ph;
struct ieee80211_header * eh;
struct pcap_pkthdr header;
const u_char * packet;
char errbuf[100];

int sock;


pthread_mutex_t mDeviceList = PTHREAD_MUTEX_INITIALIZER;

Device* deviceList; 

void pcap();
void list();
void communication();
void quit_handler(int);


int main()
{
	deviceList = 0;
	
	system("wlc monitor 1");
	
	signal(SIGINT, quit_handler);
	
	pthread_t thread_pcap, thread_list, thread_com;
	
    pthread_create(&thread_pcap, NULL, (void * (*)(void *))pcap, NULL);  
	pthread_create(&thread_list, NULL, (void * (*)(void *))list, NULL);  
	pthread_create(&thread_com, NULL, (void * (*)(void *))communication, NULL);        
    
	pthread_join(thread_pcap, NULL);
	pthread_join(thread_list, NULL);
	pthread_join(thread_com, NULL);

	return 0;
}


void pcap()
{
	handle = pcap_open_live("prism0", BUFSIZ, 1, 1000, errbuf);
	
	if (handle == NULL)
	{
		printf("Could not open the pcap interface\n");
		return;
	}
	
	printf("Thread 'Pcap' launched\n");
	
	while(1)
	{
		packet = pcap_next(handle, &header);
		
		if(((unsigned int *) packet)[0] == 0x41)
		{
			ph = (struct prism_header *) packet;
			eh = (struct ieee80211_header *) (packet + ph->msglen);
			
			// Check if FromDS flag equals 0
	    	if((eh->frame_control & 0xc0) == 0x80)
	    	{
				Device* currentDevice = 0;		
				
				pthread_mutex_lock(&mDeviceList);
				
				// find this Device in our List
				currentDevice = find_device(deviceList, eh->source_addr);
				
				// this device doesn't exist ?
				if(!currentDevice)
				{
					currentDevice = add_device(&deviceList, eh->source_addr);
					
					printf("Device created: %02x:%02x:%02x:%02x:%02x:%02x\n",
							eh->source_addr[0],
							eh->source_addr[1],
							eh->source_addr[2],
							eh->source_addr[3],
							eh->source_addr[4],
							eh->source_addr[5]);
				}
				
				// add a new sample.
				add_rssi_sample(currentDevice, (ph->rssi).data);
				
				pthread_mutex_unlock(&mDeviceList);
			}  
	  	}
	}
}


void list()
{	
	printf("Thread 'List' launched\n");
	
	while(1)
	{
		pthread_mutex_lock(&mDeviceList);
		
		delete_outdated(deviceList);
		
		pthread_mutex_unlock(&mDeviceList);

		sleep(1);
	}
}


void communication()
{
	sock = socket(PF_INET, SOCK_DGRAM, 0);

	if(sock < 0)
	{
		printf("Could not open socket\n");
		return;
	}


	struct sockaddr_in addr;

	addr.sin_family = AF_INET;
	addr.sin_port = htons(PORT);

	if(bind(sock, (struct sockaddr *) &addr, sizeof(struct sockaddr)) < 0)
	{
		printf("Error when binding socket\n");
		return;
	}

	
	// now just get my own MAC address
	struct ifreq s;

	strcpy(s.ifr_name, "eth0");
	
	ioctl(sock, SIOCGIFHWADDR, &s);
	// Mac Addr:   s.ifr_addr.sa_data[i]
	
	printf("Thread 'Communicaton' launched\n");

	
	socklen_t fromlen;
	struct sockaddr_in remote_addr;
	
	while(1)
	{
		fromlen = sizeof addr;
		char buffer[BUFFER_SIZE];
		int n = 0;
		
		if((n = recvfrom(sock, buffer, BUFFER_SIZE, 0, (struct sockaddr *) &remote_addr, &fromlen)) > 0)
		{
			buffer[n] = 0;
			
			printf("Received: %s\n", buffer);
			
			char* cmd = strtok(buffer, ";");
			
			if(strcmp(cmd, "GETOFF") == 0)
			{
				char* positionX = strtok(NULL, ";");
				
				char* positionY = strtok(NULL, ";");
				
				char* mapID = strtok(NULL, ";");
				
				char* addr = strtok(NULL, ";");
				
				printf("Received: %s, %s, %s, %s\n", positionX, positionY, mapID, addr);
				
				if(!positionX || !positionY || !mapID || !addr)
					continue;
				
				if(	strcmp(positionX, "null") == 0
						|| strcmp(positionY, "null") == 0
						|| strcmp(mapID, "null") == 0
						|| strcmp(addr, "null") == 0)
					continue;
				
				char mac_addr[6];
				
				sscanf(addr, "%x:%x:%x:%x:%x:%x", 	(int*) &mac_addr[0], 
													(int*) &mac_addr[1], 
													(int*) &mac_addr[2], 
													(int*) &mac_addr[3],
													(int*) &mac_addr[4],
													(int*) &mac_addr[5]);
				
				pthread_mutex_lock(&mDeviceList);
				
				Device* find = find_device(deviceList, mac_addr);
				
				if(find)
				{
					printf("> Device Found: %02x:%02x:%02x:%02x:%02x:%02x\n",
						(unsigned char) find->mac_address[0],
						(unsigned char) find->mac_address[1],
						(unsigned char) find->mac_address[2],
						(unsigned char) find->mac_address[3],
						(unsigned char) find->mac_address[4],
						(unsigned char) find->mac_address[5]);
				}
				
				int avg = get_average_value(find);
				
				sprintf(buffer, "RSSIO;%s;%s;%s;%s;%02x:%02x:%02x:%02x:%02x:%02x;%d", 
									positionX, 
									positionY, 
									mapID, 
									addr,
									(unsigned char) s.ifr_addr.sa_data[0],
									(unsigned char) s.ifr_addr.sa_data[1],
									(unsigned char) s.ifr_addr.sa_data[2],
									(unsigned char) s.ifr_addr.sa_data[3],
									(unsigned char) s.ifr_addr.sa_data[4],
									(unsigned char) s.ifr_addr.sa_data[5],
									avg);
				
				
				printf("sent: %s\n", buffer);
				
				remote_addr.sin_port = htons(RESPONSE_PORT);
				
				sendto(sock, buffer, BUFFER_SIZE, 0, (struct sockaddr *) &remote_addr, sizeof(struct sockaddr));
				
				
				pthread_mutex_unlock(&mDeviceList);
			}
			else if(strcmp(cmd, "GET") == 0)
			{
				// online mode
				char* addr = strtok(NULL, ";");
				
				if(!addr)
					continue;
				
				if(strcmp(addr, "null") == 0)
					continue;
				
				char mac_addr[6];
				
				sscanf(addr, "%x:%x:%x:%x:%x:%x", 	(int*) &mac_addr[0], 
													(int*) &mac_addr[1], 
													(int*) &mac_addr[2], 
													(int*) &mac_addr[3],
													(int*) &mac_addr[4],
													(int*) &mac_addr[5]);
				
				pthread_mutex_lock(&mDeviceList);
				
				Device* find = find_device(deviceList, mac_addr);
				
				if(find)
				{
					printf("> Device Found: %02x:%02x:%02x:%02x:%02x:%02x\n",
						(unsigned char) find->mac_address[0],
						(unsigned char) find->mac_address[1],
						(unsigned char) find->mac_address[2],
						(unsigned char) find->mac_address[3],
						(unsigned char) find->mac_address[4],
						(unsigned char) find->mac_address[5]);
				}
				
				int avg = get_average_value(find);
				
				sprintf(buffer, "RSS;%s;%02x:%02x:%02x:%02x:%02x:%02x;%d;", 
									addr,
									(unsigned char) s.ifr_addr.sa_data[0],
									(unsigned char) s.ifr_addr.sa_data[1],
									(unsigned char) s.ifr_addr.sa_data[2],
									(unsigned char) s.ifr_addr.sa_data[3],
									(unsigned char) s.ifr_addr.sa_data[4],
									(unsigned char) s.ifr_addr.sa_data[5],
									avg);
				
				
				printf("sent: %s\n", buffer);
				
				remote_addr.sin_port = htons(RESPONSE_PORT);
				
				sendto(sock, buffer, BUFFER_SIZE, 0, (struct sockaddr *) &remote_addr, sizeof(struct sockaddr));
				
				
				pthread_mutex_unlock(&mDeviceList);
			}
			else
			{
				printf("Unknown UDP Command\n");
			}
		}
	}

	// requ:
	// GETOFF;X;Y;mid;ZZ:ZZ:ZZ:ZZ:ZZ:ZZ

	// resp:
	// RSSI;X;Y;mid;ZZ:ZZ:ZZ:ZZ:ZZ:ZZ;AP:AP:AP:AP:AP:AP;val
	// val = avg.
	// not in list => -95

	// online:
	// GET;XX:XX:XX:XX:XX:XX

	// RSSI;XX:XX:XX:XX:XX:XX;AP:AP:AP:AP:AP:AP;val
}


void quit_handler(int s)
{
	printf("Quit...\n");
	
	if(deviceList)
		clear_device_list(&deviceList);
	
	pcap_close(handle);
	
	close(sock);
	
	exit(1); 
}


