#include "rssiList.h"


void add_device (DeviceList **l, char mac_addr[6]){
	DeviceList *newDevice = (DeviceList*) malloc(sizeof(DeviceList));
	strncpy(newDevice->dl_mac_address, mac_addr, 6);
	newDevice->dl_next = *l;
	l = &newDevice;
}
void clear_device_list(DeviceList **l){
	
}

void add_rssi_sample(DeviceList *l, int rssi_value){

}
void clear_rssi_list(DeviceList *l){

}
void delete_outdated(DeviceList *l, struct timeval current_time){

}

