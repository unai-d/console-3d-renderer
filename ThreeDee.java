import java.util.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.lang.reflect.Array;
import java.net.URL;

class Vector2i
{
	public int X, Y;

	public Vector2i(int x, int y)
	{
		X = x;
		Y = y;
	}

	public Vector2i clipRangeZeroToEnd(Vector2i end)
	{
		return clipRange(new Vector2i(0, 0), end);
	}

	public Vector2i clipRange(Vector2i start, Vector2i end)
	{
		int x = X < start.X ? start.X : (X >= end.X ? end.X - 1 : X);
		int y = Y < start.Y ? start.Y : (Y >= end.Y ? end.Y - 1 : Y);
		return new Vector2i(x, y);
	}
}

class Vector2f
{
	public float X, Y;

	public Vector2f(float x, float y)
	{
		X = x;
		Y = y;
	}

	@Override
	public String toString()
	{
		return "[" + X + ", " + Y + "]";
	}
}

class Vector3i
{
	public int X, Y, Z;

	public Vector3i(int x, int y, int z)
	{
		X = x;
		Y = y;
		Z = z;
	}

	public Vector3i multiply(float scalar)
	{
		return new Vector3i((int) (X * scalar), (int) (Y * scalar), (int) (Z * scalar));
	}
}

class Vector3f
{
	public float X, Y, Z;

	public Vector3f(float x, float y, float z)
	{
		X = x;
		Y = y;
		Z = z;
	}

	@Override
	public String toString()
	{
		return "[" + X + ", " + Y + ", " + Z + "]";
	}

	public Vector3f zProject()
	{
		float z = (float) Math.log(Z + 1);
		return new Vector3f(X / z, Y / z, Z);
	}

	public Vector4f toVector4f(float w)
	{
		return new Vector4f(X, Y, Z, w);
	}

	public Vector3i toVector3i()
	{
		return new Vector3i((int) X, (int) Y, (int) Z);
	}

	public Vector2i toVector2i()
	{
		return new Vector2i((int) X, (int) Y);
	}
}

class Vector4f
{
	public float X, Y, Z, W;

	public Vector4f(float x, float y, float z, float w)
	{
		X = x;
		Y = y;
		Z = z;
		W = w;
	}

	@Override
	public String toString()
	{
		return "[" + X + ", " + Y + ", " + Z + ", " + W + "]";
	}

	public static Vector4f fromEulerAngles(float roll, float pitch, float yaw) // roll (x), pitch (y), yaw (z)
	{
		float cr = (float) Math.cos(roll * 0.5);
		float sr = (float) Math.sin(roll * 0.5);
		float cp = (float) Math.cos(pitch * 0.5);
		float sp = (float) Math.sin(pitch * 0.5);
		float cy = (float) Math.cos(yaw * 0.5);
		float sy = (float) Math.sin(yaw * 0.5);

		return new Vector4f(sr * cp * cy - cr * sp * sy,
				cr * sp * cy + sr * cp * sy,
				cr * cp * sy - sr * sp * cy,
				cr * cp * cy + sr * sp * sy);
	}

	/*public Vector4f zProject()
	{
		float z = (float) Math.log(Z + 1);
		return new Vector4f(X / z, Y / z, Z, W / z);
	}*/

	public Vector3f xyz()
	{
		return toVector3f(false);
	}
	
	public Vector3f toVector3f(boolean normalize)
	{
		return normalize ? new Vector3f(X / W, Y / W, Z / W) : new Vector3f(X, Y, Z);
	}
}

class Matrix4x4f 
{
	public float A11, A12, A13, A14,
			A21, A22, A23, A24,
			A31, A32, A33, A34,
			A41, A42, A43, A44;

	public static final Matrix4x4f IDENTITY = new Matrix4x4f(1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1);

	public static final Matrix4x4f EMPTY = new Matrix4x4f(0, 0, 0, 0,
			0, 0, 0, 0,
			0, 0, 0, 0,
			0, 0, 0, 0);

	public Matrix4x4f(float a11, float a12, float a13, float a14,
			float a21, float a22, float a23, float a24,
			float a31, float a32, float a33, float a34,
			float a41, float a42, float a43, float a44) {
		A11 = a11;
		A12 = a12;
		A13 = a13;
		A14 = a14;

		A21 = a21;
		A22 = a22;
		A23 = a23;
		A24 = a24;

		A31 = a31;
		A32 = a32;
		A33 = a33;
		A34 = a34;

		A41 = a41;
		A42 = a42;
		A43 = a43;
		A44 = a44;
	}

	public static Matrix4x4f scale(Vector3f input)
	{
		return new Matrix4x4f(input.X, 0, 0, 0,
				0, input.Y, 0, 0,
				0, 0, input.Z, 0,
				0, 0, 0, 1);
	}

	public static Matrix4x4f rotation(Vector4f q)
	{
		Matrix4x4f ret = Matrix4x4f.IDENTITY;

		float sqx = q.X * q.X;
		float sqy = q.Y * q.Y;
		float sqz = q.Z * q.Z;
		float sqw = q.W * q.W;

		float xy = q.X * q.Y;
		float xz = q.X * q.Z;
		float xw = q.X * q.W;

		float yz = q.Y * q.Z;
		float yw = q.Y * q.W;

		float zw = q.Z * q.W;

		float s2 = 2f / (sqx + sqy + sqz + sqw);

		ret.A11 = 1f - (s2 * (sqy + sqz));
		ret.A22 = 1f - (s2 * (sqx + sqz));
		ret.A33 = 1f - (s2 * (sqx + sqy));

		ret.A12 = s2 * (xy + zw);
		ret.A21 = s2 * (xy - zw);

		ret.A31 = s2 * (xz + yw);
		ret.A13 = s2 * (xz - yw);

		ret.A32 = s2 * (yz - xw);
		ret.A23 = s2 * (yz + xw);

		return ret;
	}

	public static Matrix4x4f projection(float fov, float nearPlane, float farPlane)
	{
		float s = (float) (1 / Math.tan((fov / 2) + (Math.PI / 180)));
		float z1 = -(farPlane / (farPlane - nearPlane));
		float z2 = -((farPlane * nearPlane) / (farPlane - nearPlane));

		return new Matrix4x4f(s, 0.0f, 0.0f, 0.0f,
			0.0f, s, 0.0f, 0.0f,
			0.0f, 0.0f, z1, -1.0f,
			0.0f, 0.0f, z2, 0.0f);
	}

	public static Matrix4x4f translation(Vector3f input)
	{
		return new Matrix4x4f(1, 0, 0, input.X,
			0, 1, 0, input.Y,
			0, 0, 1, input.Z,
			0, 0, 0, 1);
	}

	public Vector4f multiply(Vector4f right)
	{
		return multiply(right, false);
	}

	public Vector4f multiply(Vector4f right, boolean normalize)
	{
		var x = (A11 * right.X) + (A12 * right.Y) + (A13 * right.Z) + (A14 * right.W);
		var y = (A21 * right.X) + (A22 * right.Y) + (A23 * right.Z) + (A24 * right.W);
		var z = (A31 * right.X) + (A32 * right.Y) + (A33 * right.Z) + (A34 * right.W);
		var w = (A41 * right.X) + (A42 * right.Y) + (A43 * right.Z) + (A44 * right.W);

		if (normalize)
		{
			x /= w;
			y /= w;
			z /= w;
			w = 1;
		}

		return new Vector4f(x, y, z, w);
	}

	public Matrix4x4f multiply(Matrix4x4f right)
	{
		// A1x*Bx1 -> A11*B11 + A12*B21 ...
		float r11 = (A11 * right.A11) + (A12 * right.A21) + (A13 * right.A31) + (A14 * right.A41);
		// A1x*Bx2 -> A11*B12 + A12*B22 ...
		float r12 = (A11 * right.A12) + (A12 * right.A22) + (A13 * right.A32) + (A14 * right.A42);
		// A1x*Bx3 -> A11*B13 + A12*B23 ...
		float r13 = (A11 * right.A13) + (A12 * right.A23) + (A13 * right.A33) + (A14 * right.A43);
		// A1x*Bx4 -> A11*B14 + A12*B24 ...
		float r14 = (A11 * right.A14) + (A12 * right.A24) + (A13 * right.A34) + (A14 * right.A44);

		// A2x*Bx1 -> A21*B12 + A22*B22 ...
		float r21 = (A21 * right.A11) + (A22 * right.A21) + (A23 * right.A31) + (A24 * right.A41);
		float r22 = (A21 * right.A12) + (A22 * right.A22) + (A23 * right.A32) + (A24 * right.A42);
		float r23 = (A21 * right.A13) + (A22 * right.A23) + (A23 * right.A33) + (A24 * right.A43);
		float r24 = (A21 * right.A14) + (A22 * right.A24) + (A23 * right.A34) + (A24 * right.A44);

		// A3x*Bx1 -> A31*B12 + A32*B22 ...
		float r31 = (A31 * right.A11) + (A32 * right.A21) + (A33 * right.A31) + (A34 * right.A41);
		float r32 = (A31 * right.A12) + (A32 * right.A22) + (A33 * right.A32) + (A34 * right.A42);
		float r33 = (A31 * right.A13) + (A32 * right.A23) + (A33 * right.A33) + (A34 * right.A43);
		float r34 = (A31 * right.A14) + (A32 * right.A24) + (A33 * right.A34) + (A34 * right.A44);

		// A4x*Bx1 -> A41*B12 + A42*B22 ...
		float r41 = (A41 * right.A11) + (A42 * right.A21) + (A43 * right.A31) + (A44 * right.A41);
		float r42 = (A41 * right.A12) + (A42 * right.A22) + (A43 * right.A32) + (A44 * right.A42);
		float r43 = (A41 * right.A13) + (A42 * right.A23) + (A43 * right.A33) + (A44 * right.A43);
		float r44 = (A41 * right.A14) + (A42 * right.A24) + (A43 * right.A34) + (A44 * right.A44);

		return new Matrix4x4f(r11, r12, r13, r14, r21, r22, r23, r24, r31, r32, r33, r34, r41, r42, r43, r44);
	}
}

class Texture
{
	int width, height;
	Vector3i[][] pixels;

	public Texture(Vector3i[] rawRgbData, int width)
	{
		this.width = width;
		height = rawRgbData.length / this.width;
		pixels = new Vector3i[this.width][height];

		for (int i = 0; i < rawRgbData.length; i++)
		{
			int x = i % width, y = i / width;
			pixels[x][y] = rawRgbData[i];
		}
	}

	public Texture(String path) throws IOException
	{
		BufferedImage buffer;
		if (path.contains(":")) // It's a URL.
		{
			URL url = new URL(path);
			InputStream iStr = url.openStream();
			buffer = ImageIO.read(iStr);
		}
		else // TODO: Is it necessary to make this distinction?
		{
			buffer = ImageIO.read(new File(path));
		}
		width = buffer.getWidth();
		height = buffer.getHeight();

		// Get all pixels and pre-process them in a Vector3i array.
		pixels = new Vector3i[width][height];
		for (int i = 0; i < width * height; i++)
		{
			int x = i % width, y = i / width;
			int p = buffer.getRGB(x % width, y % height);
			//int a = (p >> 24) & 0xff;
			int r = (p >> 16) & 0xff;
			int g = (p >> 8) & 0xff;
			int b = p & 0xff;
			pixels[x][y] = new Vector3i(r, g, b);
		}
	}

	public Vector3i getNdcPixel(Vector2f xy)
	{
		return getPixel((int) (xy.X * width), (int) (xy.Y * height));
	}

	public Vector3i getPixel(int x, int y)
	{
		return pixels[x % width][y % height];
	}
}

class Framebuffer<T>
{
	T[] buffer;
	Vector2i bufferSize;
	T defaultValue;

	float[] zBuffer;
	float farPlane = 100.0f;

	public Framebuffer(int width, int height, T defaultVal)
	{
		@SuppressWarnings("unchecked")
		final T[] a = (T[]) Array.newInstance(defaultVal.getClass(), width * height);
		buffer = a;
		
		zBuffer = new float[width * height];
		bufferSize = new Vector2i(width, height);
		defaultValue = defaultVal;
		clear();
	}

	public void clear()
	{
		fill(defaultValue);
	}

	public void fill(T value)
	{
		for (int i = 0; i < buffer.length; i++)
		{
			buffer[i] = value;
			zBuffer[i] = farPlane;
		}
	}

	public T get(int index)
	{
		return (index > 0 && index < buffer.length) ? buffer[index] : defaultValue;
	}
	
	public void safeSet(int index, T value)
	{
		if (index > 0 && index < buffer.length)
		{
			buffer[index] = value;
		}
	}

	public void safeSet(Vector2i coord, T value)
	{
		if (coord.X > 0 && coord.X < bufferSize.X &&
				coord.Y > 0 && coord.Y < bufferSize.Y)
		{
			buffer[coord.X + (coord.Y * bufferSize.X)] = value;
		}
	}

	public void safeSet(Vector3f coord, T value)
	{
		if (coord.X > 0 && coord.X < bufferSize.X &&
			coord.Y > 0 && coord.Y < bufferSize.Y &&
			coord.Z > 0.1f && coord.Z < farPlane) // TODO: This should be handled by the shader.
		{
			int address = (int) coord.X + ((int) coord.Y * bufferSize.X);
			if (coord.Z < zBuffer[address]) // fragment is nearer the last fragment.
			{
				buffer[address] = value;
				zBuffer[address] = coord.Z;
			}
		}
	}
	
	public void safeNdcPixelSet(Vector3f ndc, T value)
	{
		safeNdcPixelSet(ndc.X, ndc.Y, ndc.Z, value);
	}

	public void safeNdcPixelSet(float x, float y, float z, T value)
	{
		Vector3f fbc = ThreeDee.ndcToFbSize(x, y, z, bufferSize.X, bufferSize.Y);
		if (fbc.Z > 0.1f && fbc.Z < farPlane) // TODO: Near/far planes.
		{
			safeSet((int) fbc.X + ((int) fbc.Y * bufferSize.X), value);
		}
	}
}

class BasicShaders
{
	/*static Vector2i[] line(Vector2i a, Vector2i b)
	{
		var xLen = Math.abs(Math.abs(a.X) - Math.abs(b.X));
		var yLen = Math.abs(Math.abs(a.Y) - Math.abs(b.Y));

		Vector2i[] ret;

		ret = new Vector2i[Math.max(xLen, yLen)];
		for (int i = 0; i < ret.length; i++)
		{
			float interpFactor = i / (float) ret.length;
			int x = (int) (a.X * (1.0f - interpFactor) + b.X * interpFactor);
			int y = (int) (a.Y * (1.0f - interpFactor) + b.Y * interpFactor);
			ret[i] = new Vector2i(x, y);
		}

		return ret;
	}*/

	static Vector3f[] line(Vector3f a, Vector3f b)
	{
		float xLen = Math.abs(Math.abs(a.X) + Math.abs(b.X));
		float yLen = Math.abs(Math.abs(a.Y) + Math.abs(b.Y));
		//float zLen = Math.abs(Math.abs(a.Z) + Math.abs(b.Z)); // TODO: Take Z into account.

		int steps = (int) Math.min(Math.max(xLen, yLen), 1024);
		List<Vector3f> list = new ArrayList<Vector3f>();

		for (int i = 0; i < steps; i++)
		{
			float interpFactor = i / (float) steps;
			float z = a.Z * (1.0f - interpFactor) + b.Z * interpFactor;
			if (z < 0.0f)
			{
				continue;
			}
			float x = a.X * (1.0f - interpFactor) + b.X * interpFactor;
			float y = a.Y * (1.0f - interpFactor) + b.Y * interpFactor;
			list.add(new Vector3f(x, y, z));
		}

		Vector3f[] ret = new Vector3f[list.size()];
		list.toArray(ret);
		return ret;
	}
	
	static float edgeFunction(Vector3f a, Vector3f b, float x, float y)
	{
		return ((x - a.X) * (b.Y - a.Y) - (y - a.Y) * (b.X - a.X));
	}

	// TODO: Should this be a framebuffer method?
	// TODO: Use `XYZUV` as inputs.
	static XYZUV[] face(Vector3f a, Vector3f b, Vector3f c, Vector2f aUv, Vector2f bUv, Vector2f cUv, Vector2i framebufferSize)
	{
		List<XYZUV> list = new ArrayList<XYZUV>();
		
		float area = edgeFunction(a, b, c.X, c.Y);

		for (int y = 0; y < framebufferSize.Y; y++)
		{
			for (int x = 0; x < framebufferSize.X; x++)
			{
				float w0 = edgeFunction(a, b, x, y);
				float w1 = edgeFunction(b, c, x, y);
				float w2 = edgeFunction(c, a, x, y);

				// If `x` and `y` are inside the triangle (between `a`, `b` and `c`).
				if (w0 >= 0 && w1 >= 0 && w2 >= 0)
				{
					w0 /= area;
					w1 /= area;
					w2 /= area;

					float z = w0 * c.Z + w1 * a.Z + w2 * b.Z;
					float u = w0 * cUv.X + w1 * aUv.X + w2 * bUv.X;
					float v = w0 * cUv.Y + w1 * aUv.Y + w2 * bUv.Y;

					list.add(new XYZUV(new Vector3f(x, y, z), new Vector2f(u, v)));
				}
			}
		}

		XYZUV[] ret = new XYZUV[list.size()];
		list.toArray(ret);
		return ret;
	}
}

class XYZUV
{
	public Vector3f XYZ;
	public Vector2f UV;

	public XYZUV(Vector3f xyz, Vector2f uv)
	{
		XYZ = xyz;
		UV = uv;
	}
}

/**
 * A basic 3D renderer for an ANSI X3.64-compliant terminal.
 * 
 * @author Unai Domínguez
 */
class ThreeDee
{
	static Framebuffer<Vector3i> framebuffer;

	static Vector3f[] vertices = new Vector3f[]
	{
		// Z+
		new Vector3f(-0.5f,  0.5f, 0.5f),
		new Vector3f( 0.5f,  0.5f, 0.5f),
		new Vector3f( 0.5f, -0.5f, 0.5f),
		new Vector3f(-0.5f, -0.5f, 0.5f),

		// Z-
		new Vector3f(-0.5f,  0.5f, -0.5f),
		new Vector3f( 0.5f,  0.5f, -0.5f),
		new Vector3f( 0.5f, -0.5f, -0.5f),
		new Vector3f(-0.5f, -0.5f, -0.5f),
		
		// Y+ v
		new Vector3f( 0.5f,  0.5f, -0.5f),
		new Vector3f( 0.5f,  0.5f,  0.5f),
		new Vector3f(-0.5f,  0.5f,  0.5f),
		new Vector3f(-0.5f,  0.5f, -0.5f),
		
		// Y- v
		new Vector3f( 0.5f, -0.5f, -0.5f),
		new Vector3f( 0.5f, -0.5f,  0.5f),
		new Vector3f(-0.5f, -0.5f,  0.5f),
		new Vector3f(-0.5f, -0.5f, -0.5f),
		
		// X+
		new Vector3f( 0.5f, -0.5f,  0.5f),
		new Vector3f( 0.5f,  0.5f,  0.5f),
		new Vector3f( 0.5f,  0.5f, -0.5f),
		new Vector3f( 0.5f, -0.5f, -0.5f),
		
		// X-
		new Vector3f(-0.5f, -0.5f,  0.5f),
		new Vector3f(-0.5f,  0.5f,  0.5f),
		new Vector3f(-0.5f,  0.5f, -0.5f),
		new Vector3f(-0.5f, -0.5f, -0.5f),
	};

	static Vector2f[] textureCoordinates = new Vector2f[]
	{
		// Z+
		new Vector2f(0.0f, 1.0f),
		new Vector2f(1.0f, 1.0f),
		new Vector2f(1.0f, 0.0f),
		new Vector2f(0.0f, 0.0f),

		// Z-
		new Vector2f(0.0f, 1.0f),
		new Vector2f(1.0f, 1.0f),
		new Vector2f(1.0f, 0.0f),
		new Vector2f(0.0f, 0.0f),

		// Y+
		new Vector2f(0.0f, 1.0f),
		new Vector2f(1.0f, 1.0f),
		new Vector2f(1.0f, 0.0f),
		new Vector2f(0.0f, 0.0f),

		// Y-
		new Vector2f(0.0f, 1.0f),
		new Vector2f(1.0f, 1.0f),
		new Vector2f(1.0f, 0.0f),
		new Vector2f(0.0f, 0.0f),

		// X+
		new Vector2f(0.0f, 1.0f),
		new Vector2f(1.0f, 1.0f),
		new Vector2f(1.0f, 0.0f),
		new Vector2f(0.0f, 0.0f),

		// X-
		new Vector2f(0.0f, 1.0f),
		new Vector2f(1.0f, 1.0f),
		new Vector2f(1.0f, 0.0f),
		new Vector2f(0.0f, 0.0f),
	};

	static int[] indices = new int[]
	{
		0, 3, 1,
		1, 3, 2,

		4, 5, 7,
		5, 6, 7,
		
		8, 11, 9, //   Y+ 0, 3, 1,
		9, 11, 10, //     1, 3, 2,
		
		12, 13, 15, // Y- 0, 1, 3,
		13, 14, 15, //    1, 2, 3,
		
		16, 19, 17, // X+ 0, 3, 1,
		17, 19, 18, //    1, 3, 2,
	
		20, 21, 23, // X- 0, 1, 3,
		21, 22, 23  //    1, 2, 3,
	};

	static Texture texture;

	static Texture defaultTexture = new Texture(new Vector3i[]
	{
		new Vector3i(240, 0, 16),
		new Vector3i(0, 216, 32),
		new Vector3i(26, 0, 240),
		new Vector3i(255, 255, 0),
	}, 2);

	public static Vector3f ndcToFbSize(Vector3f input, int fbw, int fbh)
	{
		return ndcToFbSize(input.X, input.Y, input.Z, fbw, fbh);
	}

	public static Vector3f ndcToFbSize(float x, float y, float z, int fbw, int fbh)
	{
		float retX = ((x + 1.0f) / 2) * fbw;
		float retY = ((y + 1.0f) / 2) * fbh;
		return new Vector3f(retX, retY, z);
	}

	public static void setConsoleGrayscale256Color(float grayscale)
	{
		if (Float.isInfinite(grayscale))
		{
			System.out.print("\u001b[31m");
		}
		else if (grayscale < 0.0f)
		{
			System.out.print("\u001b[30m");
		}
		else if (grayscale >= 0.9f)
		{  
			System.out.print("\u001b[32m");
		}
		else
		{
			System.out.print("\u001b[38;5;" + (int) (232 + (grayscale * 16.0f)) + "m");
		}
	}

	public static String getAnsiCodeForFgRgb24(Vector3i rgb)
	{
		return getAnsiCodeForFgRgb24(rgb.X, rgb.Y, rgb.Z);
	}

	public static String getAnsiCodeForFgRgb24(int r, int g, int b)
	{
		return "\u001b[38;2;" + Math.min(r, 255) + ";" + Math.min(g, 255) + ";" + Math.min(b, 255) + "m";
	}

	public static void setConsoleCursorPosition(int x, int y)
	{
		// ANSI escape codes are mostly one-indexed when handling coordinates.
		System.out.print("\u001b[" + (y + 1) + ";" + (x + 1) + "H");
	}

	public static void main(String[] args) throws InterruptedException, IOException, Exception
	{
		boolean isWindows = System.getProperty("os.name").startsWith("Windows");

		// Enter non-canonical mode (in a not-so-cross-platform way).
		if (isWindows)
		{
			throw new Exception("Non-canonical mode not supported in Windows.");
		}
		else
		{
			String[] nonCanonicalModeCmdUnix = { "/bin/sh", "-c", "stty raw < /dev/tty" };
			Runtime.getRuntime().exec(nonCanonicalModeCmdUnix).waitFor();
		}

		// Get the terminal buffer size using ANSI escape codes.
		// FIXME: This doesn't seem to work with Windows.
		System.out.print(
			"\u001b[s" +			// Save the cursor position in an internal register.
			"\u001b[1024;1024H" +	// Move the cursor to a ridiculously high number of columns and rows.
			"\u001b[6n" +			// Print the current *actual* position of the cursor to `stdin`.
			"\u001b[u"				// Restore the cursor position that was saved earlier from that internal register.
		);

		// It's not guaranteed that the data we want will be immediately available since the terminal seems to return the data asynchronously.
		// The program basically needs to wait for the data to be available on `stdin`.
		while (System.in.available() < 6)
		{}
		
		// Get the ANSI escape code from `stdin`, containing the total number of rows and columns (in that order).
		String terminalSizeRawAnsi = "";
		while (System.in.available() > 0)
		{
			char input = (char) System.in.read();
			terminalSizeRawAnsi += input;
			if (input == 'R') // 'R' marks the end of this specific ANSI escape code.
			{
				System.err.println(terminalSizeRawAnsi);
				break;
			}
		}

		// Parse the ANSI escape code.
		String[] terminalSizeRawTup = terminalSizeRawAnsi.substring(2, terminalSizeRawAnsi.length() - 1).split("\\;");
		// Create a `Vector2i` that contains the number of columns and rows (in that order).
		Vector2i terminalSize = new Vector2i(Integer.parseInt(terminalSizeRawTup[1]), Integer.parseInt(terminalSizeRawTup[0]));
		System.out.printf("Resolution is %s×%s%n", terminalSize.X, terminalSize.Y);

		// Create the framebuffer, where all drawing operations will occur. It needs the terminal buffer size that we stored in `terminalSize`.
		framebuffer = new Framebuffer<Vector3i>(terminalSize.X, terminalSize.Y, new Vector3i(0, 0, 0));

		// Use the URL specified as an argument when calling `ThreeDee`. If no arguments were supplied, use the default texture instead.
		texture = args.length > 0 ? new Texture(args[0]) : defaultTexture;

		// Enter the main loop that takes care of reading user input, drawing the scene to the framebuffer and render the latter to the standard output.
		boolean mainLoop = true;
		int frame = 0;
		float cameraX = 0.0f, cameraY = 0.0f, cameraZ = -1.5f;
		float movementFactor = 0.5f;
		while (mainLoop)
		{
			long startFrameTimeMs = System.currentTimeMillis();

			////////////////////////
			// PROCESS USER INPUT //
			////////////////////////

			// This polls all pending characters from `stdin` each frame.
			if (System.in.available() > 0)
			{
				char input = (char) System.in.read();
				switch (input)
				{
					case 'q':
						mainLoop = false;
						break;

					case 'w':
						cameraZ += movementFactor;
						break;

					case 'W':
						cameraZ += movementFactor * 5;
						break;

					case 's':
						cameraZ -= movementFactor;
						break;

					case 'S':
						cameraZ -= movementFactor * 5;
						break;

					case 'a':
						cameraX -= movementFactor;
						break;

					case 'd':
						cameraX += movementFactor;
						break;

					case 'z':
						cameraY -= movementFactor;
						break;

					case 'x':
						cameraY += movementFactor;
						break;
				}
			}
			/*posX = (frame % (terminalSize.X * 2)) - (terminalSize.X >> 1);
			//posY = (int)(Math.sin((float)posX) * (terminalSize[1] / 2.0f) + (terminalSize[1] / 2.0f));
			posY = (int)(terminalSize.Y / 2.0f);
			
			for (int y = 0; y < terminalSize.Y - 1; y++)
			{
				for (int x = 0; x < terminalSize.X - 1; x++)
				{
					float xFactor = Math.abs(x - posX) / (terminalSize.X * 0.25f);
					float yFactor = Math.abs(y - posY) / (terminalSize.Y * 0.25f);
					framebuffer[x + (y * terminalSize.X)] = getShadeCharFromFloat(1 - Math.abs(xFactor * yFactor) + ((float)Math.random() % 0.05f));
				}
			}*/

			////////////////
			// DRAW SCENE //
			////////////////

			framebuffer.clear();

			//Matrix4x4f projectionMatrix = Matrix4x4f.projection(180.0f, 0.01f, 100.0f);
			Matrix4x4f translationMatrix = Matrix4x4f.translation(new Vector3f(-cameraX, -cameraY, -cameraZ));
			Matrix4x4f rotationMatrix = Matrix4x4f.rotation(Vector4f.fromEulerAngles(frame / 45.0f, 0, frame / 30.0f));
			Matrix4x4f aspectRatioMatrix = Matrix4x4f.scale(new Vector3f(2.25f * terminalSize.Y / (float) terminalSize.X, 1.0f, 1.0f));

			// Render triangles (3 vertices per iteration).
			for (int i = 0; i < indices.length; i += 3)
			{
				// Get triangle vertices and texture coordinates.
				var vector1 = vertices[indices[i]];
				var vector2 = vertices[indices[i + 1] % vertices.length];
				var vector3 = vertices[indices[i + 2] % vertices.length];

				var uv1 = textureCoordinates[indices[i]];
				var uv2 = textureCoordinates[indices[i + 1]];
				var uv3 = textureCoordinates[indices[i + 2]];

				// Model space to world space.
				Vector4f vector4d1 = vector1.toVector4f(1.0f);
				Vector4f vector4d2 = vector2.toVector4f(1.0f);
				Vector4f vector4d3 = vector3.toVector4f(1.0f);

				vector4d1 = rotationMatrix.multiply(vector4d1, true);
				vector4d2 = rotationMatrix.multiply(vector4d2, true);
				vector4d3 = rotationMatrix.multiply(vector4d3, true);

				vector4d1 = translationMatrix.multiply(vector4d1, true);
				vector4d2 = translationMatrix.multiply(vector4d2, true);
				vector4d3 = translationMatrix.multiply(vector4d3, true);

				vector4d1 = aspectRatioMatrix.multiply(vector4d1, true);
				vector4d2 = aspectRatioMatrix.multiply(vector4d2, true);
				vector4d3 = aspectRatioMatrix.multiply(vector4d3, true);

				vector1 = vector4d1.xyz();
				vector2 = vector4d2.xyz();
				vector3 = vector4d3.xyz();

				// World space to projection (NDC framebuffer) space.
				vector1 = vector1.zProject();
				vector2 = vector2.zProject();
				vector3 = vector3.zProject();

				// Convert XY coordinates from NDC to absolute framebuffer coordinates (Z should be left untouched).
				Vector3f fbc1 = ndcToFbSize(vector1, terminalSize.X, terminalSize.Y);
				Vector3f fbc2 = ndcToFbSize(vector2, terminalSize.X, terminalSize.Y);
				Vector3f fbc3 = ndcToFbSize(vector3, terminalSize.X, terminalSize.Y);

				// Get all the fragments from this triangle.
				var face = BasicShaders.face(fbc1, fbc2, fbc3, uv1, uv2, uv3, terminalSize);

				// Render all the fragments to the framebuffer.
				for (int j = 0; j < face.length; j++)
				{
					XYZUV fragment = face[j];
					Vector3f fragmentPosition = fragment.XYZ;
					Vector2f fragmentTexCoords = fragment.UV;
					Vector3i fragmentColor = texture.getNdcPixel(fragmentTexCoords);
					fragmentColor = fragmentColor.multiply(1.0f / fragmentPosition.Z);
					framebuffer.safeSet(fragmentPosition, fragmentColor);
				}

				// Draw triangle lines.
				/*Vector3i lineColor = new Vector3i(255, 255, 255);
				var line1 = BasicShaders.line(fbc1, fbc2);
				var line2 = BasicShaders.line(fbc2, fbc3);
				var line3 = BasicShaders.line(fbc3, fbc1);
				
				for (int j = 0; j < line1.length; j++)
				{
					framebuffer.safeSet(line1[j], lineColor);
				}
				for (int j = 0; j < line2.length; j++)
				{
					framebuffer.safeSet(line2[j], lineColor);
				}
				for (int j = 0; j < line3.length; j++)
				{
					framebuffer.safeSet(line3[j], lineColor);
				}*/

				//safeSet(framebuffer, fbc1.X + (fbc1.Y * terminalSize.X), 'o');
				/*framebuffer.safeSet(fbc1, new Vector3i(255, 0, 0));
				framebuffer.safeSet(fbc2, new Vector3i(255, 0, 0));
				framebuffer.safeSet(fbc3, new Vector3i(255, 0, 0));*/
			}

			////////////////////////
			// RENDER FRAMEBUFFER //
			////////////////////////

			Vector3i lastColor = new Vector3i(0, 0, 0);

			// Set cursor position to x=0, y=0.
			setConsoleCursorPosition(0, 0);

			for (int y = 0; y < terminalSize.Y - 1; y++)
			{
				for (int x = 0; x < terminalSize.X - 1; x++)
				{
					Vector3i value = framebuffer.get(x + (y * terminalSize.X));
					if (value != lastColor)
					{
						System.out.print(getAnsiCodeForFgRgb24(value));
					}
					System.out.print('█');
				}
				System.out.print("\u001b[1E"); // Move to the beginning of the next line.
			}

			long frameTimeMs = System.currentTimeMillis() - startFrameTimeMs;

			// Print status text on top of the rendered scene.
			setConsoleCursorPosition(2, terminalSize.Y - 1);
			System.out.print(getAnsiCodeForFgRgb24(new Vector3i(255, 255, 255)));
			System.out.print("Frame " + frame + " | " + (int) (1000 * (1.0f / frameTimeMs)) + " FPS  ");

			frame++;
		}
		
		setConsoleCursorPosition(0, 0);
		
		// Revert changes to the terminal input mode (canonical mode is a must-have when returning to the shell).
		if (!isWindows)
		{
			String[] canonicalModeCmdUnix = { "/bin/sh", "-c", "stty cooked < /dev/tty" };
			Runtime.getRuntime().exec(canonicalModeCmdUnix).waitFor();
		}
	}

	public static char getShadeCharFromDouble(double input)
	{
		return getShadeCharFromFloat((float)input);
	}

	public static char getShadeCharFromFloat(float input)
	{
		char output = ' ';
		if (Float.isInfinite(input))
		{
			output = '∞';
		}
		else if (input >= 0.2f && input < 0.4f)
		{
			output = '░';
		}
		else if (input >= 0.4f && input < 0.6f)
		{
			output = '▒';
		}
		else if (input >= 0.6f && input < 0.8f)
		{
			output = '▓';
		}
		else if (input >= 0.8f)
		{
			output = '█';
		}

		return output;
	}
}