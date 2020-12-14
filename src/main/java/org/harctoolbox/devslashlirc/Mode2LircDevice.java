/*
Copyright (C) 2016 Bengt Martensson.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or (at
your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program. If not, see http://www.gnu.org/licenses/.
*/

package org.harctoolbox.devslashlirc;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class models a /dev/lirc device of the mode2 type, that is, capable of
 * sending and/or receiving according to raw timing information.
 */
public class Mode2LircDevice extends LircDevice implements IMode2 {

    private static final int defaultCaptureSize = 250;
    private static final int defaultEndTimeout = 200;
    private static final Logger logger = Logger.getLogger(Mode2LircDevice.class.getName());
    private native static long newMode2LircDevice(String deviceName);

    public static void main(String[] args) {
        int nec1_frequency = 38400;
        int[] nec1_122_29 = {
            9024, 4512, 564, 564, 564, 1692, 564, 564, 564, 1692, 564, 1692, 564, 1692, 564, 1692, 564, 564, 564, 1692, 564, 564, 564, 1692, 564, 564, 564, 564, 564, 564, 564, 564, 564, 1692, 564, 1692, 564, 564, 564, 1692, 564, 1692, 564, 1692, 564, 564, 564, 564, 564, 564, 564, 564, 564, 1692, 564, 564, 564, 564, 564, 564, 564, 1692, 564, 1692, 564, 1692, 564, 39756
        };
        LircHardware.loadLibrary();
        try (Mode2LircDevice device = new Mode2LircDevice("/dev/lirc0", 10000, 100, 222)) {
            System.out.println(device.getVersion());
            device.open();
            device.report();
            System.out.println(device);
            System.out.println(">>>>>>>>>>>>>>>>>> Now shoot IR!");
            int[] result = device.receive();
            for (int i = 0; i < result.length; i++)
                System.out.print((i % 2 == 0 ? "+" : "-") + result[i] + " ");
            System.out.println();
            device.setTransmitterMask(1);
            device.send(nec1_122_29, nec1_frequency);
        } catch (LircDeviceException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public Mode2LircDevice(String deviceName, int beginTimeout, int captureSize, int endTimeout) {
        super(deviceName, beginTimeout, newMode2LircDevice(deviceName));
        setMaxCaptureLength(captureSize);
        setEndingTimeout(endTimeout);
    }

    public Mode2LircDevice() throws LircDeviceException {
        this(defaultDeviceName, defaultBeginTimeout, defaultCaptureSize, defaultEndTimeout);
    }

    public Mode2LircDevice(String deviceName) throws LircDeviceException {
        this(deviceName, defaultBeginTimeout, defaultCaptureSize, defaultEndTimeout);
    }

    public Mode2LircDevice(String deviceName, boolean verbose, Integer timeout) throws LircDeviceException {
        this(fixDeviceName(deviceName), timeout != null ? timeout : defaultBeginTimeout, defaultCaptureSize, defaultEndTimeout);
    }

    private native int getRecResolutionNative();
    private native void setSendCarrierNative(int frequency);
    private native void sendNative(int[] data);
    private native int readNative();
    private native int[] receiveNative();


    @Override
    protected native boolean openNative();

    @Override
    public native boolean canSetSendCarrier();

    @Override
    public native boolean canGetRecResolution();

    @Override
    public void send(int[] data) throws NotSupportedException {
        if (!canSend())
            throw new NotSupportedException("Hardware does not support sending");
        sendNative(data);
    }

    @Override
    public void send(int[] data, int frequency) throws NotSupportedException {
        setSendCarrier(frequency);
        send(data);
    }

    @Override
    public int getRecResolution() throws NotSupportedException {
        if (!canGetRecResolution())
            throw new NotSupportedException("getResoultion not supported by driver");
        return getRecResolutionNative();
    }

    @Override
    public void setSendCarrier(int frequency) throws NotSupportedException {
        if (!canSetSendCarrier())
            throw new NotSupportedException("setSendCarrier not supported by driver");
        setSendCarrierNative(frequency);
    }

    public int read() throws NotSupportedException {
        if (!canRec())
            throw new NotSupportedException("Hardware does not support receiving");
        return readNative();
    }

    @Override
    public int[] receive() throws NotSupportedException {
        if (!canRec())
            throw new NotSupportedException("Hardware does not support receiving");
        return receiveNative();
    }

    public native void report();

    @Override
    public final void setEndingTimeout(int timeout) {
        setEndTimeout(timeout);
    }

    private final native void setEndTimeout(int timeout); // TODO change name in code

    @Override
    public final native void setMaxCaptureLength(int maxCaptureLength);

    @Override
    public String toString() {
        return "mode2 "
                + (canSetSendCarrier() ? " setCarr." : "")
                + (canGetRecResolution() ? " getRecRes." : "")
                + super.toString();
    }
}
