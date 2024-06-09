/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

import java.nio.ByteBuffer;
import org.lwjgl.util.glu.GLU; // from legacy LWJGL2.9.4
import FastNoiseLite;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

/**
 * File: Worms.java
 * Created on 9 июн. 2024 г., 17:30:36
 *
 * @author LWJGL2
 */
public class Worms {

    private long window;
    private int width = 800;
    private int height = 600;

    private Worm[] worms;
    private int wormCount = 32;

    public static void main(String[] args) {
        System.setProperty("joml.format", "false"); // joml fix
        new Worms().run();
    }

    public void run() {
        init();
        loop();
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        window = GLFW.glfwCreateWindow(width, height, "(without libnoise) Demonstration - Perlin worms", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        Display.centerWindow(window);
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();

        worms = new Worm[wormCount];
        for (int i = 0; i < wormCount; i++) {
            worms[i] = new Worm(i);
        }
    }
 
    // Ширина текстуры червяка.
    private static final int TEXTURE_WIDTH = 16;

    // Высота текстуры червяка.
    private static final int TEXTURE_HEIGHT = 16;

    // Данные текстуры червяка в формате RGB.
    private static final byte[] TEXTURE_DATA = {(byte) 123, (byte) 89, (byte) 63, (byte) 64, (byte) 95, (byte) 66, (byte) 42, (byte) 64, (byte) 65, (byte) 46, (byte) 25, (byte) 64, (byte) 66, (byte) 47, (byte) 26, (byte) 64, (byte) 68, (byte) 49, (byte) 25, (byte) 64, (byte) 82, (byte) 60, (byte) 32, (byte) 64, (byte) 116, (byte) 97, (byte) 64, (byte) 64, (byte) 127, (byte) 104, (byte) 73, (byte) 64, (byte) 103, (byte) 76, (byte) 49, (byte) 64, (byte) 98, (byte) 73, (byte) 51, (byte) 64, (byte) 96, (byte) 73, (byte) 50, (byte) 64, (byte) 95, (byte) 72, (byte) 47, (byte) 64, (byte) 99, (byte) 75, (byte) 57, (byte) 64, (byte) 116, (byte) 81, (byte) 58, (byte) 64, (byte) 129, (byte) 94, (byte) 67, (byte) 64, (byte) 117, (byte) 83, (byte) 57, (byte) 64, (byte) 203, (byte) 165, (byte) 119, (byte) 255, (byte) 199, (byte) 162, (byte) 111, (byte) 255, (byte) 162, (byte) 116, (byte) 65, (byte) 255, (byte) 163, (byte) 123, (byte) 79, (byte) 255, (byte) 159, (byte) 132, (byte) 93, (byte) 255, (byte) 143, (byte) 108, (byte) 66, (byte) 255, (byte) 214, (byte) 182, (byte) 138, (byte) 255, (byte) 206, (byte) 180, (byte) 139, (byte) 255, (byte) 175, (byte) 142, (byte) 91, (byte) 255, (byte) 195, (byte) 157, (byte) 109, (byte) 255, (byte) 210, (byte) 176, (byte) 126, (byte) 255, (byte) 172, (byte) 131, (byte) 77, (byte) 255, (byte) 182, (byte) 154, (byte) 119, (byte) 255, (byte) 181, (byte) 151, (byte) 122, (byte) 255, (byte) 213, (byte) 176, (byte) 136, (byte) 255, (byte) 197, (byte) 160, (byte) 120, (byte) 255, (byte) 219, (byte) 185, (byte) 139, (byte) 255, (byte) 224, (byte) 195, (byte) 147, (byte) 255, (byte) 189, (byte) 143, (byte) 82, (byte) 255, (byte) 203, (byte) 159, (byte) 106, (byte) 255, (byte) 228, (byte) 202, (byte) 157, (byte) 255, (byte) 198, (byte) 155, (byte) 94, (byte) 255, (byte) 201, (byte) 157, (byte) 108, (byte) 255, (byte) 225, (byte) 195, (byte) 147, (byte) 255, (byte) 211, (byte) 171, (byte) 111, (byte) 255, (byte) 217, (byte) 178, (byte) 128, (byte) 255, (byte) 232, (byte) 201, (byte) 162, (byte) 255, (byte) 213, (byte) 176, (byte) 116, (byte) 255, (byte) 220, (byte) 184, (byte) 133, (byte) 255, (byte) 224, (byte) 197, (byte) 161, (byte) 255, (byte) 229, (byte) 202, (byte) 157, (byte) 255, (byte) 215, (byte) 180, (byte) 125, (byte) 255, (byte) 219, (byte) 185, (byte) 125, (byte) 255, (byte) 222, (byte) 190, (byte) 128, (byte) 255, (byte) 196, (byte) 147, (byte) 85, (byte) 255, (byte) 210, (byte) 166, (byte) 111, (byte) 255, (byte) 226, (byte) 197, (byte) 142, (byte) 255, (byte) 199, (byte) 152, (byte) 89, (byte) 255, (byte) 211, (byte) 172, (byte) 125, (byte) 255, (byte) 213, (byte) 175, (byte) 111, (byte) 255, (byte) 187, (byte) 134, (byte) 72, (byte) 255, (byte) 203, (byte) 159, (byte) 89, (byte) 255, (byte) 227, (byte) 192, (byte) 142, (byte) 255, (byte) 225, (byte) 193, (byte) 136, (byte) 255, (byte) 203, (byte) 159, (byte) 89, (byte) 255, (byte) 227, (byte) 192, (byte) 142, (byte) 255, (byte) 225, (byte) 193, (byte) 136, (byte) 255, (byte) 210, (byte) 168, (byte) 103, (byte) 255, (byte) 187, (byte) 130, (byte) 68, (byte) 255, (byte) 185, (byte) 130, (byte) 68, (byte) 255, (byte) 168, (byte) 114, (byte) 54, (byte) 255, (byte) 191, (byte) 137, (byte) 74, (byte) 255, (byte) 192, (byte) 142, (byte) 74, (byte) 255, (byte) 166, (byte) 109, (byte) 53, (byte) 255, (byte) 211, (byte) 172, (byte) 116, (byte) 255, (byte) 185, (byte) 131, (byte) 65, (byte) 255, (byte) 159, (byte) 103, (byte) 44, (byte) 255, (byte) 181, (byte) 130, (byte) 59, (byte) 255, (byte) 196, (byte) 139, (byte) 72, (byte) 255, (byte) 179, (byte) 124, (byte) 53, (byte) 255, (byte) 177, (byte) 128, (byte) 54, (byte) 255, (byte) 198, (byte) 139, (byte) 74, (byte) 255, (byte) 194, (byte) 138, (byte) 74, (byte) 255, (byte) 173, (byte) 115, (byte) 54, (byte) 255, (byte) 154, (byte) 102, (byte) 37, (byte) 255, (byte) 166, (byte) 110, (byte) 45, (byte) 255, (byte) 162, (byte) 107, (byte) 44, (byte) 255, (byte) 164, (byte) 110, (byte) 44, (byte) 255, (byte) 168, (byte) 114, (byte) 47, (byte) 255, (byte) 159, (byte) 106, (byte) 42, (byte) 255, (byte) 181, (byte) 138, (byte) 74, (byte) 255, (byte) 159, (byte) 114, (byte) 38, (byte) 255, (byte) 154, (byte) 106, (byte) 30, (byte) 255, (byte) 173, (byte) 126, (byte) 48, (byte) 255, (byte) 173, (byte) 125, (byte) 50, (byte) 255, (byte) 125, (byte) 85, (byte) 18, (byte) 255, (byte) 149, (byte) 105, (byte) 33, (byte) 255, (byte) 166, (byte) 110, (byte) 41, (byte) 255, (byte) 166, (byte) 109, (byte) 40, (byte) 255, (byte) 147, (byte) 93, (byte) 31, (byte) 255, (byte) 154, (byte) 110, (byte) 25, (byte) 255, (byte) 149, (byte) 105, (byte) 24, (byte) 255, (byte) 144, (byte) 100, (byte) 23, (byte) 255, (byte) 152, (byte) 106, (byte) 24, (byte) 255, (byte) 152, (byte) 107, (byte) 25, (byte) 255, (byte) 144, (byte) 100, (byte) 26, (byte) 255, (byte) 139, (byte) 99, (byte) 29, (byte) 255, (byte) 136, (byte) 98, (byte) 21, (byte) 255, (byte) 131, (byte) 89, (byte) 18, (byte) 255, (byte) 144, (byte) 103, (byte) 28, (byte) 255, (byte) 149, (byte) 105, (byte) 35, (byte) 255, (byte) 111, (byte) 76, (byte) 20, (byte) 255, (byte) 125, (byte) 91, (byte) 19, (byte) 255, (byte) 147, (byte) 105, (byte) 20, (byte) 255, (byte) 149, (byte) 110, (byte) 21, (byte) 255, (byte) 142, (byte) 102, (byte) 17, (byte) 255, (byte) 136, (byte) 96, (byte) 21, (byte) 255, (byte) 131, (byte) 90, (byte) 20, (byte) 255, (byte) 125, (byte) 84, (byte) 18, (byte) 255, (byte) 131, (byte) 92, (byte) 20, (byte) 255, (byte) 133, (byte) 93, (byte) 20, (byte) 255, (byte) 125, (byte) 87, (byte) 21, (byte) 255, (byte) 122, (byte) 88, (byte) 23, (byte) 255, (byte) 122, (byte) 86, (byte) 21, (byte) 255, (byte) 119, (byte) 82, (byte) 20, (byte) 255, (byte) 125, (byte) 89, (byte) 21, (byte) 255, (byte) 131, (byte) 94, (byte) 27, (byte) 255, (byte) 103, (byte) 73, (byte) 21, (byte) 255, (byte) 103, (byte) 75, (byte) 16, (byte) 255, (byte) 119, (byte) 86, (byte) 15, (byte) 255, (byte) 125, (byte) 89, (byte) 15, (byte) 255, (byte) 125, (byte) 88, (byte) 15, (byte) 255, (byte) 111, (byte) 79, (byte) 22, (byte) 255, (byte) 114, (byte) 79, (byte) 22, (byte) 255, (byte) 111, (byte) 73, (byte) 21, (byte) 255, (byte) 108, (byte) 75, (byte) 22, (byte) 255, (byte) 114, (byte) 80, (byte) 22, (byte) 255, (byte) 108, (byte) 75, (byte) 20, (byte) 255, (byte) 101, (byte) 71, (byte) 19, (byte) 255, (byte) 101, (byte) 75, (byte) 22, (byte) 255, (byte) 106, (byte) 74, (byte) 21, (byte) 255, (byte) 103, (byte) 75, (byte) 17, (byte) 255, (byte) 106, (byte) 78, (byte) 17, (byte) 255, (byte) 96, (byte) 68, (byte) 18, (byte) 255, (byte) 94, (byte) 69, (byte) 18, (byte) 255, (byte) 103, (byte) 72, (byte) 16, (byte) 255, (byte) 101, (byte) 72, (byte) 15, (byte) 255, (byte) 108, (byte) 79, (byte) 17, (byte) 255, (byte) 96, (byte) 73, (byte) 21, (byte) 255, (byte) 108, (byte) 77, (byte) 25, (byte) 255, (byte) 106, (byte) 73, (byte) 26, (byte) 255, (byte) 106, (byte) 72, (byte) 25, (byte) 255, (byte) 103, (byte) 73, (byte) 22, (byte) 255, (byte) 98, (byte) 69, (byte) 19, (byte) 255, (byte) 94, (byte) 65, (byte) 18, (byte) 255, (byte) 96, (byte) 68, (byte) 20, (byte) 255, (byte) 101, (byte) 70, (byte) 20, (byte) 255, (byte) 91, (byte) 66, (byte) 17, (byte) 255, (byte) 98, (byte) 71, (byte) 20, (byte) 255, (byte) 94, (byte) 67, (byte) 21, (byte) 255, (byte) 98, (byte) 72, (byte) 21, (byte) 255, (byte) 108, (byte) 78, (byte) 21, (byte) 255, (byte) 108, (byte) 78, (byte) 21, (byte) 255, (byte) 101, (byte) 73, (byte) 20, (byte) 255, (byte) 87, (byte) 66, (byte) 18, (byte) 255, (byte) 103, (byte) 78, (byte) 27, (byte) 255, (byte) 114, (byte) 85, (byte) 32, (byte) 255, (byte) 111, (byte) 79, (byte) 30, (byte) 255, (byte) 114, (byte) 84, (byte) 29, (byte) 255, (byte) 103, (byte) 74, (byte) 25, (byte) 255, (byte) 98, (byte) 72, (byte) 22, (byte) 255, (byte) 101, (byte) 73, (byte) 22, (byte) 255, (byte) 94, (byte) 68, (byte) 20, (byte) 255, (byte) 94, (byte) 67, (byte) 21, (byte) 255, (byte) 106, (byte) 76, (byte) 23, (byte) 255, (byte) 98, (byte) 71, (byte) 21, (byte) 255, (byte) 96, (byte) 69, (byte) 19, (byte) 255, (byte) 101, (byte) 75, (byte) 21, (byte) 255, (byte) 111, (byte) 84, (byte) 23, (byte) 255, (byte) 96, (byte) 73, (byte) 21, (byte) 255, (byte) 76, (byte) 58, (byte) 14, (byte) 255, (byte) 89, (byte) 69, (byte) 21, (byte) 255, (byte) 106, (byte) 83, (byte) 29, (byte) 255, (byte) 106, (byte) 80, (byte) 30, (byte) 255, (byte) 106, (byte) 78, (byte) 29, (byte) 255, (byte) 103, (byte) 74, (byte) 28, (byte) 255, (byte) 106, (byte) 78, (byte) 28, (byte) 255, (byte) 108, (byte) 81, (byte) 28, (byte) 255, (byte) 96, (byte) 69, (byte) 23, (byte) 255, (byte) 94, (byte) 67, (byte) 20, (byte) 255, (byte) 96, (byte) 69, (byte) 20, (byte) 255, (byte) 108, (byte) 80, (byte) 26, (byte) 255, (byte) 101, (byte) 72, (byte) 24, (byte) 255, (byte) 87, (byte) 62, (byte) 17, (byte) 255, (byte) 82, (byte) 62, (byte) 14, (byte) 255, (byte) 82, (byte) 63, (byte) 15, (byte) 255, (byte) 59, (byte) 44, (byte) 9, (byte) 255, (byte) 61, (byte) 47, (byte) 11, (byte) 255, (byte) 66, (byte) 51, (byte) 14, (byte) 255, (byte) 68, (byte) 53, (byte) 17, (byte) 255, (byte) 72, (byte) 50, (byte) 19, (byte) 255, (byte) 76, (byte) 51, (byte) 21, (byte) 255, (byte) 82, (byte) 58, (byte) 22, (byte) 255, (byte) 87, (byte) 62, (byte) 23, (byte) 255, (byte) 87, (byte) 60, (byte) 21, (byte) 255, (byte) 89, (byte) 62, (byte) 21, (byte) 255, (byte) 89, (byte) 62, (byte) 19, (byte) 255, (byte) 98, (byte) 72, (byte) 25, (byte) 255, (byte) 101, (byte) 73, (byte) 26, (byte) 255, (byte) 85, (byte) 62, (byte) 17, (byte) 255, (byte) 59, (byte) 44, (byte) 7, (byte) 255, (byte) 58, (byte) 44, (byte) 7, (byte) 255, (byte) 44, (byte) 33, (byte) 6, (byte) 255, (byte) 42, (byte) 32, (byte) 6, (byte) 255, (byte) 44, (byte) 34, (byte) 8, (byte) 255, (byte) 48, (byte) 35, (byte) 11, (byte) 255, (byte) 51, (byte) 35, (byte) 12, (byte) 255, (byte) 53, (byte) 35, (byte) 13, (byte) 255, (byte) 56, (byte) 38, (byte) 14, (byte) 255, (byte) 56, (byte) 38, (byte) 13, (byte) 255, (byte) 56, (byte) 36, (byte) 12, (byte) 255, (byte) 63, (byte) 43, (byte) 15, (byte) 255, (byte) 70, (byte) 49, (byte) 15, (byte) 255, (byte) 65, (byte) 45, (byte) 11, (byte) 255, (byte) 59, (byte) 40, (byte) 9, (byte) 255, (byte) 51, (byte) 35, (byte) 7, (byte) 255, (byte) 42, (byte) 30, (byte) 4, (byte) 255, (byte) 45, (byte) 32, (byte) 5, (byte) 255, (byte) 31, (byte) 19, (byte) 4, (byte) 255, (byte) 23, (byte) 15, (byte) 3, (byte) 255, (byte) 22, (byte) 15, (byte) 4, (byte) 255, (byte) 26, (byte) 18, (byte) 6, (byte) 255, (byte) 31, (byte) 20, (byte) 7, (byte) 255, (byte) 32, (byte) 21, (byte) 7, (byte) 255, (byte) 30, (byte) 19, (byte) 7, (byte) 255, (byte) 37, (byte) 23, (byte) 7, (byte) 255, (byte) 36, (byte) 22, (byte) 8, (byte) 255, (byte) 40, (byte) 25, (byte) 8, (byte) 255, (byte) 41, (byte) 27, (byte) 8, (byte) 255, (byte) 33, (byte) 20, (byte) 4, (byte) 255, (byte) 28, (byte) 17, (byte) 2, (byte) 255, (byte) 24, (byte) 14, (byte) 2, (byte) 255, (byte) 28, (byte) 17, (byte) 3, (byte) 255, (byte) 30, (byte) 18, (byte) 4, (byte) 255, (byte) 6, (byte) 2, (byte) 1, (byte) 64, (byte) 6, (byte) 2, (byte) 1, (byte) 64, (byte) 5, (byte) 3, (byte) 1, (byte) 64, (byte) 6, (byte) 3, (byte) 1, (byte) 64, (byte) 6, (byte) 4, (byte) 1, (byte) 64, (byte) 6, (byte) 3, (byte) 1, (byte) 64, (byte) 7, (byte) 3, (byte) 1, (byte) 64, (byte) 8, (byte) 5, (byte) 1, (byte) 64, (byte) 10, (byte) 5, (byte) 1, (byte) 64, (byte) 10, (byte) 5, (byte) 1, (byte) 64, (byte) 11, (byte) 6, (byte) 1, (byte) 64, (byte) 14, (byte) 8, (byte) 1, (byte) 64, (byte) 23, (byte) 13, (byte) 3, (byte) 64, (byte) 19, (byte) 11, (byte) 2, (byte) 64, (byte) 7, (byte) 3, (byte) 1, (byte) 64, (byte) 6, (byte) 3, (byte) 1, (byte) 64};

    private static void extractPixel(char[] data, byte[] pixel) {
        pixel[0] = (byte) (((data[0] - 33) << 2) | ((data[1] - 33) >> 4));
        pixel[1] = (byte) ((((data[1] - 33) & 0xF) << 4) | ((data[2] - 33) >> 2));
        pixel[2] = (byte) ((((data[2] - 33) & 0x3) << 6) | ((data[3] - 33)));
    }

    public static ByteBuffer convertToByteBuffer(byte[] texture) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(texture.length);
        buffer.put(texture);
        buffer.position(0);
        return buffer;
    }

    public static int createTexture() {

        // Загружаем текстуру на видеокарту и создаем mipmap'ы.
        int textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        int status = GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, GL11.GL_RGBA8, TEXTURE_WIDTH, TEXTURE_HEIGHT, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, convertToByteBuffer(TEXTURE_DATA));
        if (status != GL11.GL_NO_ERROR) {
            System.err.println("Error MipMap create: " + GLU.gluErrorString(status));
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        return textureID;
    }
    private int frame = 0;

    private void loop() {
        GL11.glClearColor(0.1f, 0.15f, 0.3f, 1.0f);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        int createTexture = createTexture();

        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            int[] widthBuf = new int[1];
            int[] heightBuf = new int[1];
            GLFW.glfwGetWindowSize(window, widthBuf, heightBuf);

            final int w = widthBuf[0];
            final int h = heightBuf[0];

            GL11.glViewport(0, 0, w, h);

//            // No need for perspective since the worms are rendered in 2D.
            double aspect = (double) h / (double) w;
            {
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                GL11.glOrtho(-1.0f, 1.0f, -1.0f * aspect, 1.0f * aspect, 0.0f, 1.0f);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, createTexture);
                GL14.glActiveTexture(GL14.GL_TEXTURE0);
                GL11.glEnable(GL11.GL_TEXTURE_2D);

                // Draw all the worms.
                for (Worm worm : worms) {
                    worm.draw();
                    worm.update();
                }

                // Show the frame count in the console window.
                frame++;

                GL11.glPopMatrix();
                GL11.glFlush();
            }

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    public static class Worm {

        // Default worm lateral speed.
        private static final double WORM_LATERAL_SPEED = (1.0 / 8192.0);

        // Default length of a worm segment, in screen units.
        private static final double WORM_SEGMENT_LENGTH = (1.0 / 64.0);

        // Default segment count for each worm.
        private static final int WORM_SEGMENT_COUNT = 112;

        // Default worm speed.
        private static final double WORM_SPEED = (3.0 / 2048.0);

        // Default worm thickness.
        private static final double WORM_THICKNESS = (4.0 / 256.0);

        // Default "twistiness" of the worms.
        private static final double WORM_TWISTINESS = (4.0 / 256.0);

        private Vector3d m_headNoisePos;
        private Vector2d m_headScreenPos;
        private double m_lateralSpeed;
        private FastNoiseLite m_noise;
        private int m_segmentCount;
        private double m_segmentLength;
        private double m_speed;
        private double m_thickness;
        private double m_twistiness;

        public Worm(int i) {
            m_headNoisePos = new Vector3d(7.0 / 2048.0, 1163.0 / 2048.0, 409.0 / 2048.0);

            m_noise = new FastNoiseLite(i);
            m_noise.SetFrequency(1.2f);
            m_noise.SetFractalLacunarity(2.375f);
            m_noise.SetFractalOctaves(3);
            m_noise.SetFractalGain(0.5f);
            m_noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);

            m_headScreenPos = new Vector2d(0.0, 0.0);
            m_lateralSpeed = WORM_LATERAL_SPEED;
            m_segmentCount = WORM_SEGMENT_COUNT;
            m_segmentLength = WORM_SEGMENT_LENGTH;
            m_speed = WORM_SPEED;
            m_thickness = WORM_THICKNESS;
            m_twistiness = WORM_TWISTINESS;

            FastNoiseLite noise = new FastNoiseLite(i);
            noise.SetFrequency(0.08f);
            noise.SetFractalLacunarity(2.375f);
            noise.SetFractalOctaves(16);
            noise.SetFractalGain(0.5f);
            noise.SetNoiseType(FastNoiseLite.NoiseType.Value);

            m_headScreenPos.x = noise.GetNoise(i + 1000, i + 2000, i + 3000);
            m_headScreenPos.y = noise.GetNoise(i + 1001, i + 2001, i + 3001);
        }

        public double getTaperAmount(int segment) {
            double curSegment = (double) segment;
            double halfSegmentCount = (double) m_segmentCount / 2.0;
            double baseTaperAmount = 1.0 - Math.abs((curSegment / halfSegmentCount) - 1.0);
            return Math.sqrt(baseTaperAmount); // sqrt better defines the tapering.
        }

        public void setHeadScreenPos(Vector2d pos) {
            m_headScreenPos = pos;
        }

        public void setLateralSpeed(double lateralSpeed) {
            m_lateralSpeed = lateralSpeed;
        }

        public void setSeed(int seed) {
            m_noise.SetSeed(seed);
        }

        public void setSegmentCount(double segmentCount) {
            m_segmentCount = (int) segmentCount;
        }

        public void setSegmentLength(double segmentLength) {
            m_segmentLength = segmentLength;
        }

        public void setSpeed(double speed) {
            m_speed = speed;
        }

        public void setThickness(double thickness) {
            m_thickness = thickness;
        }

        public void setTwistiness(double twistiness) {
            m_twistiness = twistiness;
        }

        public void update() {
            double noiseValue = m_noise.GetNoise((float) m_headNoisePos.x, (float) m_headNoisePos.y, (float) m_headNoisePos.z);
            m_headScreenPos.x -= (Math.cos(noiseValue * 2.0 * Math.PI) * m_speed);
            m_headScreenPos.y -= (Math.sin(noiseValue * 2.0 * Math.PI) * m_speed);

            m_headNoisePos.x -= m_speed * 2.0;
            m_headNoisePos.y += m_lateralSpeed;
            m_headNoisePos.z += m_lateralSpeed;

            clamp(m_headScreenPos, -1.0, 1.0);
        }

        public void draw() { // comments translated using google translate :O
            // Начинаем рисовать червяка с помощью треугольной полосы.
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

            // Позиция текущего сегмента, который рисуется, в экранном пространстве.
            Vector2d curSegmentScreenPos = new Vector2d(m_headScreenPos);

            // Ширина тела червяка в текущем сегменте.
            Vector2d offsetPos = new Vector2d();

            // Координаты входного значения в "шумовом пространстве", которые задают
            // угол текущего сегмента.
            Vector3d curNoisePos = new Vector3d();

            // Вектор, перпендикулярный центру сегмента; используется для определения
            // позиции краев тела червяка.
            Vector2d curNormalPos = new Vector2d();

            for (int curSegment = 0; curSegment < m_segmentCount; curSegment++) {

                // Получаем значение шума Perlin для этого сегмента на основе номера сегмента.
                // Это значение интерпретируется как угол в радианах.
                curNoisePos.x = m_headNoisePos.x + (curSegment * m_twistiness);
                curNoisePos.y = m_headNoisePos.y;
                curNoisePos.z = m_headNoisePos.z;
                double noiseValue = m_noise.GetNoise(
                        (float) curNoisePos.x,
                        (float) curNoisePos.y,
                        (float) curNoisePos.z);

                // Определяем ширину тела червяка в этом сегменте.
                double taperAmount = getTaperAmount(curSegment) * m_thickness;

                // Определяем смещение этого сегмента от предыдущего сегмента, преобразуя угол
                // из модуля шума Perlin в координату (x, y).
                offsetPos.x = Math.cos(noiseValue * 2.0 * Math.PI);
                offsetPos.y = Math.sin(noiseValue * 2.0 * Math.PI);

                // Определяем координаты каждого угла сегмента.
                curNormalPos.x = (-offsetPos.y) * taperAmount;
                curNormalPos.y = (offsetPos.x) * taperAmount;
                offsetPos.x *= m_segmentLength;
                offsetPos.y *= m_segmentLength;
                double x0 = curSegmentScreenPos.x + curNormalPos.x;
                double y0 = curSegmentScreenPos.y + curNormalPos.y;
                double x1 = curSegmentScreenPos.x - curNormalPos.x;
                double y1 = curSegmentScreenPos.y - curNormalPos.y;

                // Рисуем сегмент с помощью OpenGL.
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glTexCoord2f((float) curSegment, 0.0f);
                GL11.glVertex2d(x0, y0);
                GL11.glTexCoord2f((float) curSegment, 1.0f);
                GL11.glVertex2d(x1, y1);

                // Подготавливаем следующий сегмент.
                ++curSegment;
                curSegmentScreenPos.x += offsetPos.x;
                curSegmentScreenPos.y += offsetPos.y;
            }

            // Заканчиваем рисовать червяка.
            GL11.glEnd();
        }

        private void clamp(Vector2d value, double lowerBound, double upperBound) {
            value.x = Math.max(lowerBound, Math.min(value.x, upperBound));
            value.y = Math.max(lowerBound, Math.min(value.y, upperBound));
        }
    }
}
