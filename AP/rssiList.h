#include <sys/time.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <math.h>

typedef struct _Rssi
{
	struct timeval date;
	int value;
	struct _Rssi* next;
} Rssi;

typedef struct _Device
{
	char mac_address[6];
	Rssi* rssi_list;
	struct _Device* next;
} Device;


Device* add_device(Device** l, char* mac_addr);

void clear_device_list(Device** l);

Device* find_device(Device* l, char* mac_address);

void add_rssi_sample(Device* l, int rssi_value);

void clear_rssi_list(Device* l);

void delete_outdated(Device* l);

int get_average_value(Device* l);

void print_list(Device* l);

int timeval_subtract(struct timeval* result, struct timeval* x, struct timeval* y);


