package cn.easyar.samples.helloarmultitargetmt;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.easyar.Matrix44F;
import cn.easyar.Vec2F;

public class TriangleRender {
    //着色器的编写
    private String  vertex_shader_code ="uniform mat4 trans;"+"attribute vec4 vPosition;"+ "uniform mat4 proj;"+
    "void main() {"+
         "gl_Position = proj*trans*vPosition;\n"+
    "}";
    private String color_shader_code = "precision mediump float;"+
   " uniform vec4 vColor;"+
    "void main() {"+
        "gl_FragColor = vColor;"+
    "}";
    //
    private float triangleCoords[][] = {
            {0.5f,  0.5f, 0.0f}, // top
            {-0.5f, -0.5f, 0.0f}, // bottom left
            {0.5f, -0.5f, 0.0f}  // bottom right
    };
    private int pos_trans_box;
    private int pos_proj_box;
    private float color[] = {1.0f,1.0f,1.0f,1.0f};
    private FloatBuffer vertexBuffer;
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    static final int COORDS_PER_VERTEX = 3;
    private int vbo_coord_box;
    private int vbo_color_box;
    public void init(){

        //创建程序
        mProgram = GLES20.glCreateProgram();
        //点Shader处理
        int vertShader =  GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertShader,vertex_shader_code);
        GLES20.glCompileShader(vertShader);
        //片元Shader的处理
        int fragShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragShader,color_shader_code);
        GLES20.glCompileShader(fragShader);
        //链接起来
        GLES20.glAttachShader(mProgram,vertShader);
        GLES20.glAttachShader(mProgram,fragShader);
        GLES20.glLinkProgram(mProgram);
        //加载程序到GLES20的环境中
        GLES20.glUseProgram(mProgram);
        //申请底层缓存
        vbo_coord_box = generateOneBuffer();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_coord_box);
        FloatBuffer cube_vertices_buffer = FloatBuffer.wrap(flatten(triangleCoords));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_vertices_buffer.limit() * 4, cube_vertices_buffer, GLES20.GL_DYNAMIC_DRAW);
        /*vbo_color_box = generateOneBuffer();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_color_box);
        FloatBuffer cube_colors_buffer = FloatBuffer.wrap(flatten(color));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_colors_buffer.limit() * 4, cube_colors_buffer, GLES20.GL_DYNAMIC_DRAW);*/
        //获取点句柄成员
        mPositionHandle = GLES20.glGetAttribLocation(mProgram,"vPosition");
        //获取颜色的句柄成员
        mColorHandle = GLES20.glGetUniformLocation(mProgram,"vColor");
        pos_trans_box = GLES20.glGetUniformLocation(mProgram, "trans");
        pos_proj_box = GLES20.glGetUniformLocation(mProgram, "proj");
    }
    public void render(Matrix44F projectionMatrix, Matrix44F cameraview, Vec2F size)
    {

        float size0 = size.data[0];
        float size1 = size.data[1];
        //顶点
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_coord_box);
       /* float triangleCoords[][] = {
                {0.5f*size0,  0.5f*size1, 0.0f}, // top
                {-0.5f*size0, -0.5f*size1, 0.0f}, // bottom left
                {0.5f*size0, -0.5f*size1, 0.0f}  // bottom right
        };*/
        FloatBuffer cube_vertices_buffer = FloatBuffer.wrap(flatten(triangleCoords));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_vertices_buffer.limit() * 4, cube_vertices_buffer, GLES20.GL_DYNAMIC_DRAW);
        //颜色
       /* GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_color_box);
        FloatBuffer cube_colors_buffer = FloatBuffer.wrap(flatten(color));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_colors_buffer.limit() * 4, cube_colors_buffer, GLES20.GL_DYNAMIC_DRAW);*/
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glUniformMatrix4fv(pos_trans_box, 1, false, cameraview.data, 0);
        GLES20.glUniformMatrix4fv(pos_proj_box, 1, false, projectionMatrix.data, 0);
        GLES20.glUseProgram(mProgram);
        //准备三角形的数据
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle,COORDS_PER_VERTEX,GLES20.GL_FLOAT,false,COORDS_PER_VERTEX*4,0);
        //绘制三角形的颜色
        //GLES20.glUniformMatrix4fv(mColorHandle, 1, false, cameraview.data, 0);
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,3);
        //禁止顶点数组的句柄
        // GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
    private int generateOneBuffer()
    {
        int[] buffer = {0};
        GLES20.glGenBuffers(1, buffer, 0);
        return buffer[0];
    }
    private float[] flatten(float[][] a)
    {
        int size = 0;
        for (int k = 0; k < a.length; k += 1) {
            size += a[k].length;
        }
        float[] l = new float[size];
        int offset = 0;
        for (int k = 0; k < a.length; k += 1) {
            System.arraycopy(a[k], 0, l, offset, a[k].length);
            offset += a[k].length;
        }
        return l;
    }
    private int[] flatten(int[][] a)
    {
        int size = 0;
        for (int k = 0; k < a.length; k += 1) {
            size += a[k].length;
        }
        int[] l = new int[size];
        int offset = 0;
        for (int k = 0; k < a.length; k += 1) {
            System.arraycopy(a[k], 0, l, offset, a[k].length);
            offset += a[k].length;
        }
        return l;
    }
    private short[] flatten(short[][] a)
    {
        int size = 0;
        for (int k = 0; k < a.length; k += 1) {
            size += a[k].length;
        }
        short[] l = new short[size];
        int offset = 0;
        for (int k = 0; k < a.length; k += 1) {
            System.arraycopy(a[k], 0, l, offset, a[k].length);
            offset += a[k].length;
        }
        return l;
    }
    private byte[] flatten(byte[][] a)
    {
        int size = 0;
        for (int k = 0; k < a.length; k += 1) {
            size += a[k].length;
        }
        byte[] l = new byte[size];
        int offset = 0;
        for (int k = 0; k < a.length; k += 1) {
            System.arraycopy(a[k], 0, l, offset, a[k].length);
            offset += a[k].length;
        }
        return l;
    }
    //private byte[] flatten(List)
    private byte[] byteArrayFromIntArray(int[] a)
    {
        byte[] l = new byte[a.length];
        for (int k = 0; k < a.length; k += 1) {
            l[k] = (byte)(a[k] & 0xFF);
        }
        return l;
    }

}
