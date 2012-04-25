#include <pcap.h>
#include "prism.h"
#include "rssiList.h"

/* Content omitted */

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

/* Content omitted */

pcap_t * handle = NULL;
struct prism_header *ph;
struct ieee80211_header * eh;
struct pcap_pkthdr header;
const u_char * packet;
char errbuf[100];

DeviceList *deviceList; 

/* Content omitted */
int main(){
	handle = pcap_open_live("prism0", BUFSIZ, 1, 1000, errbuf);
	if ( handle == NULL ) {
	  printf("Could not open pcap on interface\n");
	  return -1;
	}

	/* Content omitted */

	while ( 1 ) {
	  packet = pcap_next(handle, &header);
	  //printf("%d",((unsigned int *) packet)[0]);
	  if ( ((unsigned int *) packet)[0] == 0x41 ) {
	    ph = (struct prism_header *) packet;
	    eh = (struct ieee80211_header *) (packet + ph->msglen);
	    // Check if FromDS flag equals 0
	    //printf("%d",eh->frame_control);
	    if ( (eh->frame_control & 0xc0) == 0x80 ) {
		/* Do something with (ph->rssi).data */
		if(eh->source_addr[0] == 0 
		 && eh->source_addr[1] == 0x26
		 && eh->source_addr[2] == 0x4A
		 && eh->source_addr[3] == 0xD4
		 && eh->source_addr[4] == 0xD4
		 && eh->source_addr[5] == 0xC3){
			printf("rssi: %d\n",(ph->rssi).data);
			printf("sourceAddr: %x:%x:%x:%x:%x:%x\n",eh->source_addr[0],eh->source_addr[1],eh->source_addr[2],eh->source_addr[3],eh->source_addr[4],eh->source_addr[5]);
		}
		  
	    }
	  }
	}

	/* Content omitted */
	pcap_close ( handle );

	/* Content omitted */
}

