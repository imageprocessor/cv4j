package com.cv4j.core.io;
import java.io.IOException;
import java.io.OutputStream;

/** Writes a raw image described by a FileInfo object to an OutputStream. */
public class ImageWriter {
	private FileInfo fi;
	
	public ImageWriter (FileInfo fi) {
		this.fi = fi;
	}
	
	
	void write8BitImage(OutputStream out, byte[] pixels)  throws IOException {
		int bytesWritten = 0;
		int size = fi.width*fi.height;
		int count = 8192;
		
		while (bytesWritten<size) {
			if ((bytesWritten + count)>size)
				count = size - bytesWritten;
			//System.out.println(bytesWritten + " " + count + " " + size);
			out.write(pixels, bytesWritten, count);
			bytesWritten += count;
		}
	}
	
	void write8BitStack(OutputStream out, Object[] stack)  throws IOException {
		for (int i=0; i<fi.nImages; i++) {
			//IJ.showStatus("Writing: " + (i+1) + "/" + fi.nImages);
			write8BitImage(out, (byte[])stack[i]);
			//IJ.showProgress((double)(i+1)/fi.nImages);
		}
	}


	void write16BitImage(OutputStream out, short[] pixels)  throws IOException {
		long bytesWritten = 0L;
		long size = 2L*fi.width*fi.height;
		int count = 8192;
		byte[] buffer = new byte[count];

		while (bytesWritten<size) {
			if ((bytesWritten + count)>size)
				count = (int)(size-bytesWritten);
			int j = (int)(bytesWritten/2L);
			int value;
			if (fi.intelByteOrder)
				for (int i=0; i < count; i+=2) {
					value = pixels[j];
					buffer[i] = (byte)value;
					buffer[i+1] = (byte)(value>>>8);
					j++;
				}
			else
				for (int i=0; i < count; i+=2) {
					value = pixels[j];
					buffer[i] = (byte)(value>>>8);
					buffer[i+1] = (byte)value;
					j++;
				}
			out.write(buffer, 0, count);
			bytesWritten += count;
		}
	}
	

	void writeRGB48Image(OutputStream out, Object[] stack)  throws IOException {
		short[] r = (short[])stack[0];
		short[] g = (short[])stack[1];
		short[] b = (short[])stack[2];
		int size = fi.width*fi.height;
		int count = fi.width*6;
		byte[] buffer = new byte[count];
		for (int line=0; line<fi.height; line++) {
			int index2 = 0;
			int index1 = line*fi.width;
			int value;
			if (fi.intelByteOrder) {
				for (int i=0; i<fi.width; i++) {
					value = r[index1];
					buffer[index2++] = (byte)value;
					buffer[index2++] = (byte)(value>>>8);
					value = g[index1];
					buffer[index2++] = (byte)value;
					buffer[index2++] = (byte)(value>>>8);
					value = b[index1];
					buffer[index2++] = (byte)value;
					buffer[index2++] = (byte)(value>>>8);
					index1++;
				}
			} else {
				for (int i=0; i<fi.width; i++) {
					value = r[index1];
					buffer[index2++] = (byte)(value>>>8);
					buffer[index2++] = (byte)value;
					value = g[index1];
					buffer[index2++] = (byte)(value>>>8);
					buffer[index2++] = (byte)value;
					value = b[index1];
					buffer[index2++] = (byte)(value>>>8);
					buffer[index2++] = (byte)value;
					index1++;
				}
			}
			out.write(buffer, 0, count);
		}
	}

	void writeFloatImage(OutputStream out, float[] pixels)  throws IOException {
		long bytesWritten = 0L;
		long size = 4L*fi.width*fi.height;
		int count = 8192;
		byte[] buffer = new byte[count];
		int tmp;

		while (bytesWritten<size) {
			if ((bytesWritten + count)>size)
				count = (int)(size-bytesWritten);
			int j = (int)(bytesWritten/4L);
			if (fi.intelByteOrder)
				for (int i=0; i < count; i+=4) {
					tmp = Float.floatToRawIntBits(pixels[j]);
					buffer[i]   = (byte)tmp;
					buffer[i+1] = (byte)(tmp>>8);
					buffer[i+2] = (byte)(tmp>>16);
					buffer[i+3] = (byte)(tmp>>24);
					j++;
				}
			else
				for (int i=0; i < count; i+=4) {
					tmp = Float.floatToRawIntBits(pixels[j]);
					buffer[i]   = (byte)(tmp>>24);
					buffer[i+1] = (byte)(tmp>>16);
					buffer[i+2] = (byte)(tmp>>8);
					buffer[i+3] = (byte)tmp;
					j++;
				}
			out.write(buffer, 0, count);
			bytesWritten += count;
		}
	}
	

	void writeRGBImage(OutputStream out, int[] pixels)  throws IOException {
		long bytesWritten = 0L;
		long size = 3L*fi.width*fi.height;
		int count = fi.width*24;
		byte[] buffer = new byte[count];
		while (bytesWritten<size) {
			if ((bytesWritten+count)>size)
				count = (int)(size-bytesWritten);
			int j = (int)(bytesWritten/3L);
			for (int i=0; i<count; i+=3) {
				buffer[i]   = (byte)(pixels[j]>>16);	//red
				buffer[i+1] = (byte)(pixels[j]>>8);	//green
				buffer[i+2] = (byte)pixels[j];		//blue
				j++;
			}
			out.write(buffer, 0, count);
			bytesWritten += count;
			//showProgress((double)bytesWritten/size);
		}
	}
	
}

