#include <sys/time.h>
#include <stdlib.h>
#include <string.h>

typedef struct _RssiList{
	struct timeval rl_date;
	int rl_rssi_value;
	struct _RssiList *rl_next;
} RssiList;

typedef struct _DeviceList{
	char dl_mac_address[6];
	RssiList *dl_rssi_list;
	struct _DeviceList *dl_next;
} DeviceList;

void add_device (DeviceList **l, char mac_addr[6]);
void clear_device_list(DeviceList **l);

void add_rssi_sample(DeviceList *l, int rssi_value);
void clear_rssi_list(DeviceList *l);
void delete_outdated(DeviceList *l, struct timeval current_time);
