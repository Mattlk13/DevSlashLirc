#include "Mode2LircDevice.h"

#define LIRC_DEVICE "/dev/lirc0"

/*
 *
 */
int main(int argc, char** argv) {
    (void) argc;
    (void) argv;

    Mode2LircDevice lirconian;
    lirconian.open();
    lirconian.report();
    lirconian.setTransmitterMask(1);
}
