package slaynash.sgengine.shaders;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import slaynash.sgengine.Configuration;

public class ShaderVR extends ShaderProgram {

	public ShaderVR() {
		super(Configuration.getAbsoluteInstallPath()+"/"+Configuration.getRelativeShaderPath(), "modern/modernVR.vs", "modern/modernVR.fs");
		setShadowShader(new ShadowShader());
	}

	private int colorTexture_location;
	private int normalTexture_location;
	private int specularTexture_location;
	private int lightShadows_location[];

	@Override
	protected void getAllUniformLocations() {
		colorTexture_location = super.getUniformLocation("textureDiffuse");
		normalTexture_location = super.getUniformLocation("textureNormal");
		specularTexture_location = super.getUniformLocation("textureSpecular");
		
		super.getUniformLocation("mMatrix");
		super.getUniformLocation("vMatrix");
		super.getUniformLocation("pMatrix");
		
		super.getUniformLocation("shineDamper");
		super.getUniformLocation("reflectivity");
		
		super.getUniformLocation("far_plane");
		
		lightShadows_location = new int[Configuration.MAX_LIGHTS];
		
		for(int i=0;i<Configuration.MAX_LIGHTS;i++){
			super.getUniformLocation("lightPosition["+i+"]");
			super.getUniformLocation("lightColour["+i+"]");
			super.getUniformLocation("attenuation["+i+"]");
			lightShadows_location[i] = super.getUniformLocation("lightShadows["+i+"]");
		}
	}

	@Override
	protected void connectTextureUnits() {
		GL20.glUniform1i(colorTexture_location, ShaderManager.TEXTURE_COLOR);
		GL20.glUniform1i(normalTexture_location, ShaderManager.TEXTURE_NORMAL);
		GL20.glUniform1i(specularTexture_location, ShaderManager.TEXTURE_SPECULAR);

		for(int i=0;i<Configuration.MAX_LIGHTS;i++){
			GL20.glUniform1i(lightShadows_location[i], ShaderManager.TEXTURE_SHADOWSMIN+i);
		}
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
	}

	@Override
	public void prepare() {
		bindDataDirect("far_plane", Configuration.getZFar());
	}

	@Override
	public void stop() {
		/*
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		*/
		GL30.glBindVertexArray(0);
	}

	@Override
	public void bindModel(int modelID) {
		GL30.glBindVertexArray(modelID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
	}
	
	@Override
	public boolean isCastingShadow() {
		return true;
	}
}
