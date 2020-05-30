package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.*;
import java.io.InputStream;

public class Renderer {

    private static final float FOV = 73;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;

    AngelCodeFont angelCodeFont;

    public Renderer(StaticShader shader) {
        createProjectionMatrix();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();

        InputStream inputStream = ResourceLoader.getResourceAsStream("font/Minecraft.ttf");

        try {
            awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            angelCodeFont = new AngelCodeFont(ResourceLoader.getResource("font/font.fnt").getPath(), ResourceLoader.getResource("font/font.png").getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prepare() {
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        GL11.glMatrixMode(GL11.GL_PROJECTION);

        GL11.glLoadIdentity();
        GLU.gluPerspective(45, (float) Display.getWidth() / Display.getHeight(), 0.1f, 5000.0f);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        //GL11.glClearColor(0.93f, 0.93f, 0.93f, 0);
    }

    public void prepareGUI() {
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();

        GLU.gluOrtho2D(0.0f, Display.getWidth(), Display.getHeight(), 0.0f);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        //GL11.glTranslatef(0.375f, 0.375f, 0.0f);

        GL11.glDisable(GL11.GL_DEPTH_TEST);

    }

    public void render(GameObject gameObject, StaticShader shader) {
        for(GameObject child : gameObject.getChildren()) {
            render(child, shader);
        }
        shader.start();

        TexturedModel texturedModel = gameObject.getModel();

        if(texturedModel == null) {
            return;
        };

        RawModel rawModel = texturedModel.getRawModel();

        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        Matrix4f transformationMatrix = gameObject.getTransformationMatrix();
        shader.loadTransformationMatrix(transformationMatrix);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getTextureID());

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);


        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        //GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL13.GL_SAMPLE_ALPHA_TO_COVERAGE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc ( GL11.GL_SRC_ALPHA, GL11.GL_ONE );
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        try {
            GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        } catch (Exception e) {}

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        shader.stop();
    }

    public void renderGUI(GUIObject guiObject, StaticShader shader) {
        for(GUIObject child : guiObject.getGUIChildren()) {
            renderGUI(child, shader);
        }

        shader.start();

        TexturedModel texturedModel = guiObject.getModel();

        if(texturedModel == null) {
            return;
        };

        RawModel rawModel = texturedModel.getRawModel();

        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        Matrix4f transformationMatrix = guiObject.getTransformationMatrix();
        shader.loadTransformationMatrix(transformationMatrix);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getTextureID());

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);


        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        //GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL13.GL_SAMPLE_ALPHA_TO_COVERAGE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc ( GL11.GL_SRC_ALPHA, GL11.GL_ONE );
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        try {
            GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        shader.stop();
    }

    //    InputStream inputStream = ResourceLoader.getResourceAsStream("font/Minecraft.ttf");
    TrueTypeFont font;
    Font awtFont;
    public void renderStringLegacy(Vector2f position, String text, org.newdawn.slick.Color color) {
//        System.out.println("rendering " + text);
//        System.out.println(position);
//        System.out.println(angelCodeFont);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        font = new TrueTypeFont(awtFont.deriveFont(80), false);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

        GL11.glPushMatrix();
        font.drawString(position.x + 1, position.y + 1, text, new org.newdawn.slick.Color(0, 0, 0, 0.7f * color.a)); //x, y, string to draw, color
        font.drawString(position.x, position.y, text, color); //x, y, string to draw, color
        //GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void renderString(Vector2f position, String text, org.newdawn.slick.Color color) {
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

        int stringWidth = angelCodeFont.getWidth(text);

        //GL11.glPushMatrix();
        angelCodeFont.drawString(position.x + 2, position.y + 2, text, new org.newdawn.slick.Color(0, 0, 0, 0.7f * color.a)); //x, y, string to draw, color
        TextureImpl.unbind();
        angelCodeFont.drawString(position.x , position.y, text, color); //x, y, string to draw, color
        //GL11.glDisable(GL11.GL_TEXTURE_2D);
        //GL11.glPopMatrix();
//        GL11.glDisable(GL11.GL_BLEND);
        TextureImpl.unbind();
    }

    public void renderCenteredString(Vector2f position, String text, org.newdawn.slick.Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

        int stringWidth = angelCodeFont.getWidth(text);

        //GL11.glPushMatrix();
        angelCodeFont.drawString((position.x + 2) - (stringWidth / 2), position.y + 2, text, new org.newdawn.slick.Color(0, 0, 0, 0.7f * color.a)); //x, y, string to draw, color
        angelCodeFont.drawString(position.x - (stringWidth / 2), position.y, text, color); //x, y, string to draw, color
        //GL11.glDisable(GL11.GL_TEXTURE_2D);
        //GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);
        TextureImpl.bindNone();
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }
}
