PREFIX=/home/lo53/openwrt/staging_dir/toolchain-mipsel_gcc3.4.6
GCC=$(PREFIX)/bin/mipsel-linux-gcc
LD=$(PREFIX)/bin/mipsel-linux-gcc
ALLFLAGS=-Wall -Werror -O2 -I$(PREFIX)/include/ -I$(PREFIX)/../mipsel/usr/include/ -L$(PREFIX)/lib/ -L$(PREFIX)/../mipsel/usr/lib/ -lpcap -lm -pthread
GCCFLAGS=-Wall -Werror -O2 -I$(PREFIX)/include/ -I$(PREFIX)/../mipsel/usr/include/
LDFLAGS=-L$(PREFIX)/lib/ -L$(PREFIX)/../mipsel/usr/lib/ -lpcap -lm -pthread

pcap: pcap.c
	$(GCC) $(ALLFLAGS) pcap.c rssiList.c -o pcap

%.o : %.c %.h
	$(GCC) $(GCCFLAGS) -o $@ -c $<

clean:
	rm -f *.o
	rm -f rssi-report

backupclean:
	rm -f *~

