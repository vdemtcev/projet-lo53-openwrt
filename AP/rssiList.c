#include "rssiList.h"


// OK
Device* add_device (Device** l, char* mac_addr)
{
	Device* newDevice = (Device*) malloc(sizeof(Device));
	
	if(!newDevice)
		printf("DEBUG: New Device = null\n");
	
	strncpy(newDevice->mac_address, mac_addr, 6);
	
	newDevice->rssi_list = 0;
	
	newDevice->next = *l;
	
	*l = newDevice;
	
	return newDevice;
}


// OK
void clear_device_list(Device** l)
{
	while(*l)
	{
		Device* tmp = (*l)->next;
		
		// don't forget to free the rssi values.
		clear_rssi_list(*l);
		
		free(*l);
		
		*l = tmp;
	}
	
	*l = 0;
}


// OK
void add_rssi_sample(Device* l, int rssi_value)
{
	if(!l)
		return;
	
	Rssi* newRssi = (Rssi*) malloc(sizeof(Rssi));
	
	if(!newRssi)
		printf("DEBUG: New Rssi = null\n");
	
	newRssi->value = rssi_value;
	
	gettimeofday(&newRssi->date, NULL);
	
	newRssi->next = l->rssi_list;
	
	l->rssi_list = newRssi;
}


// OK
void clear_rssi_list(Device* l)
{
	if(!l)
		return;
	
	while(l->rssi_list)
	{
		Rssi* rssi_tmp = l->rssi_list->next;
		
		free(l->rssi_list);
		
		l->rssi_list = rssi_tmp;
	}
}


// OK
void delete_outdated(Device* l)
{
	if(!l || !l->rssi_list)
		return;
	
	struct timeval now, diff;
	
	gettimeofday(&now, NULL);
	
	Rssi* first = l->rssi_list;
	
	Rssi* prec = l->rssi_list;
	
	do
	{
		int result = timeval_subtract(&diff, &now, &l->rssi_list->date);
		
		if(result == 0)
		{
			if(diff.tv_sec >= 1)
			{
				if(l->rssi_list == first)
				{
					free(l->rssi_list);
					
					l->rssi_list = 0;
					
					return;
				} 
				else
				{
					prec->next = l->rssi_list->next;
					
					free(l->rssi_list);
					
					l->rssi_list = prec;
				}
				
			}
		}
		
		prec = l->rssi_list;
	}
	while( (l->rssi_list = l->rssi_list->next) != NULL);
	
	
	l->rssi_list = first;
}


// OK
Device* find_device(Device* l, char* mac_address)
{
	if(!l)
		return 0;
	
	do
	{	
		if(strncmp(l->mac_address, mac_address, 6) == 0)
		{
			return l;
		}
	}
	while((l = l->next) != 0);
	
	return 0;
}


// OK
int get_average_value(Device* l)
{
	if(!l)
		return -95;
	
	float avg = 0.0f;
	int count = 0;
	
	Rssi* rssi = l->rssi_list;
	
	while(rssi)
	{
		// Convert in Watts
		// W = 10^(dB/10)
		avg += pow(10, (rssi->value/10.0f));
		
		count++;
		
		rssi = rssi->next;
	}
	
	if(count == 0)
		return -95;
	
	
	avg = avg / count;
	
	return 10 * log10(avg);
}


// OK
void print_list(Device* l)
{
	while(l)
	{
		
		printf("-> 1 Device: %02x:%02x:%02x:%02x:%02x:%02x\n",
				l->mac_address[0],
				l->mac_address[1],
				l->mac_address[2],
				l->mac_address[3],
				l->mac_address[4],
				l->mac_address[5]);
		
		Rssi* rssi = l->rssi_list;
		
		while(rssi)
		{
			printf("\t- Rssi Val: %d, date: %d\n", rssi->value, (int)rssi->date.tv_sec);
			
			rssi = rssi->next;
		}
		
		l = l->next;
	}
}


// http://stackoverflow.com/questions/1444428/time-stamp-in-the-c-programming-language
int timeval_subtract(struct timeval* result, struct timeval* x, struct timeval* y)
{
	/* Perform the carry for the later subtraction by updating y. */
	if (x->tv_usec < y->tv_usec)
	{
		int nsec = (y->tv_usec - x->tv_usec) / 1000000 + 1;
		y->tv_usec -= 1000000 * nsec;
		y->tv_sec += nsec;
	}
	
	if (x->tv_usec - y->tv_usec > 1000000)
	{
		int nsec = (x->tv_usec - y->tv_usec) / 1000000;
		y->tv_usec += 1000000 * nsec;
		y->tv_sec -= nsec;
	}

	/* Compute the time remaining to wait.
	tv_usec is certainly positive. */
	result->tv_sec = x->tv_sec - y->tv_sec;
	result->tv_usec = x->tv_usec - y->tv_usec;

	/* Return 1 if result is negative. */
	return x->tv_sec < y->tv_sec;
}


