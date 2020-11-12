package xyz.asitanokibou.common.lang.utils;

import java.io.*;

//copy from spring-core : org.springframework.util.SerializationUtils
/**
 * Static utilities for serialization and deserialization.
 *
 * @author Dave Syer
 * @since 3.0.5
 */
public abstract class SerializationUtils {

	/**
	 * Serialize the given object to a byte array.
	 * @param object the object to serialize
	 * @return an array of bytes representing the object in a portable fashion
	 */
	
	public static byte[] serialize( Object object) {
		if (object == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(object);
			oos.flush();
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
		}
		return baos.toByteArray();
	}

	/**
	 * Deserialize the byte array into an object.
	 * @param bytes a serialized object
	 * @return the result of deserialize the bytes
	 */
	
	public static Object deserialize( byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
			return ois.readObject();
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Failed to deserialize object", ex);
		}
		catch (ClassNotFoundException ex) {
			throw new IllegalStateException("Failed to deserialize object type", ex);
		}
	}

}
