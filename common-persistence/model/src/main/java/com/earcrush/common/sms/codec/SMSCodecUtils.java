package com.itsoninc.das.common.sms.codec;

import java.nio.ByteBuffer;

import org.apache.commons.lang3.Validate;

abstract class SMSCodecUtils {

	protected static void putUnsignedByte(ByteBuffer buffer, int val) {
		Validate.inclusiveBetween(0, 0xFF, val);
		buffer.put((byte)val);
	}

	protected static int getUnsignedByte(ByteBuffer buffer) {
		return buffer.get() & 0xFF;
	}

	protected static void putUnsignedShort(ByteBuffer buffer, int val) {
		Validate.inclusiveBetween(0, 0xFFFF, val);
		buffer.putShort((short)val);
	}

	protected static int getUnsignedShort(ByteBuffer buffer) {
		return buffer.getShort() & 0xFFFF;
	}

	protected static void putUnsignedInt(ByteBuffer buffer, long val) {
		Validate.inclusiveBetween(0L, 0xFFFFFFFFL, val);
		buffer.putInt((int)val);
	}

	protected static long getUnsignedInt(ByteBuffer buffer) {
		return buffer.getInt() & 0xFFFFFFFFL;
	}
}
