package toolbox;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class Image {
	public static ByteBuffer load(String path) {
        ByteBuffer image;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer comp = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            image = STBImage.stbi_load("res/"+path+".png", w, h, comp, 4);
            if (image == null) {
                System.err.println("Image '" + path + "' could not be found!");
            }
        }
        return image;
    }
}
