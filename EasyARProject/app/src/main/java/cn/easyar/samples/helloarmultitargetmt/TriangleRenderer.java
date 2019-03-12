package cn.easyar.samples.helloarmultitargetmt;

import android.media.tv.TvContract;
import android.opengl.GLES20;
import android.util.Log;
import android.view.SurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.easyar.Matrix44F;
import cn.easyar.Vec2F;

public class TriangleRenderer {
    private FloatBuffer vertexBuffer;
    private final String vertexShaderCode ="uniform mat4 trans;\n"
            + "uniform mat4 proj;\n"
            + "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = proj*trans*vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private int mProgram;

    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            -0.5f,  0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };

    private int mPositionHandle;
    private int mColorHandle;

    private float[] mViewMatrix=new float[16];

    //顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    private int mMatrixHandler;

    //设置颜色，依次为红绿蓝和透明通道
    float color[] = { 1.0f, 0.0f, 0.0f, 1.0f };

    public void init()
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(
                triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);
        pos_trans_box = GLES20.glGetUniformLocation(mProgram, "trans");
        pos_proj_box = GLES20.glGetUniformLocation(mProgram, "proj");

    }
    private int pos_trans_box;
    private int pos_proj_box;
    public void render(Matrix44F projectionMatrix, Matrix44F cameraview, Vec2F size)
    {

        Log.e("第一次：","大小"+vertexBuffer.capacity());
      for(int i=0;i<triangleCoords.length;i++)
       {
           if(i%3==0)
               triangleCoords[i]*=size.data[0];
           if(i%3==1)
               triangleCoords[i]*=size.data[1];
       }
        //vertexBuffer.clear();
        vertexBuffer.wrap(triangleCoords);
        //vertexBuffer.position(0);
        Log.e("第二次:","大小"+vertexBuffer.capacity());
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);

        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniformMatrix4fv(pos_proj_box, 1, false, projectionMatrix.data, 0);
        GLES20.glUniformMatrix4fv(pos_trans_box, 1, false, cameraview.data, 0);
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
    public int  loadShader(int type,String shaderCode)
    {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并且编译
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;

    }
}
