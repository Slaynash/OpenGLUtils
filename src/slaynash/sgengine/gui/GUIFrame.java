package slaynash.sgengine.gui;

import org.lwjgl.util.vector.Vector2f;

import slaynash.sgengine.gui.text2d.Text2d;
import slaynash.sgengine.maths.Vector2i;
import slaynash.sgengine.models.Renderable2dModel;
import slaynash.sgengine.models.utils.VaoManager;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.textureUtils.TextureDef;
import slaynash.sgengine.textureUtils.TextureManager;
import slaynash.sgengine.utils.UserInputUtil;

public class GUIFrame extends GUIElement {
	
	private int level;
	private boolean mouseInClose = false;
	protected static int topPadding = 16;
	protected static int bottomPadding = 4;
	protected static int leftPadding = 4;
	protected static int rightPadding = 4;
	private boolean renderInside = true;
	private Text2d title;
	private boolean dragged = false;
	private Vector2i mouseOldPos;
	
	private TextureDef texBack;
	private TextureDef texBottom;
	private TextureDef texTop;
	private TextureDef texTopFocused;
	private TextureDef texSide;
	private TextureDef texClose;
	
	private Renderable2dModel modelBack;
	private Renderable2dModel modelBottom;
	private Renderable2dModel modelTop;
	private Renderable2dModel modelSides;
	private Renderable2dModel modelClose;

	public GUIFrame(int x, int y, int width, int height, int location) {
		super(x, y, Math.max(width, leftPadding+rightPadding), Math.max(height, topPadding+bottomPadding), null, true, location);
		level = GUIManager.addTopLevel(location);
		containerPadding = new Vector2i(leftPadding, topPadding);
		containerSize = new Vector2i(this.getWidth()-leftPadding-rightPadding, this.getHeight()-topPadding-bottomPadding);


		
		texBack = TextureManager.getTextureDef("res/textures/gui/frame/frame_background.png", TextureManager.TEXTURE_DIFFUSE);
		texBottom = TextureManager.getTextureDef("res/textures/gui/frame/frame_bottom.png", TextureManager.TEXTURE_DIFFUSE);
		texTop = TextureManager.getTextureDef("res/textures/gui/frame/frame_top.png", TextureManager.TEXTURE_DIFFUSE);
		texTopFocused = TextureManager.getTextureDef("res/textures/gui/frame/frame_top_focused.png", TextureManager.TEXTURE_DIFFUSE);
		texSide = TextureManager.getTextureDef("res/textures/gui/frame/frame_side.png", TextureManager.TEXTURE_DIFFUSE);
		texClose = TextureManager.getTextureDef("res/textures/gui/frame/frame_close.png", TextureManager.TEXTURE_DIFFUSE);
		
		
		
		float[] verticesBack = new float[12];
		float uvs[] = new float[]{0,0,1,0,1,1,1,1,0,1,0,0};
		
		verticesBack[0] = leftPadding;
		verticesBack[1] = topPadding;
		verticesBack[2] = -rightPadding+width;
		verticesBack[3] = topPadding;
		verticesBack[4] = -rightPadding+width;
		verticesBack[5] = -bottomPadding+height;

		verticesBack[6] = -rightPadding+width;
		verticesBack[7] = -bottomPadding+height;
		verticesBack[8] = leftPadding;
		verticesBack[9] = -bottomPadding+height;
		verticesBack[10] = leftPadding;
		verticesBack[11] = topPadding;
		
		modelBack = new Renderable2dModel(VaoManager.loadToVao2d(verticesBack, uvs), texBack);
		
		float[] verticesBottom = new float[12];
		
		verticesBottom[0] = 0;
		verticesBottom[1] = height-bottomPadding;
		verticesBottom[2] = width;
		verticesBottom[3] = height-bottomPadding;
		verticesBottom[4] = width;
		verticesBottom[5] = height;

		verticesBottom[6] = width;
		verticesBottom[7] = height;
		verticesBottom[8] = 0;
		verticesBottom[9] = height;
		verticesBottom[10] = 0;
		verticesBottom[11] = height-bottomPadding;
		
		modelBottom = new Renderable2dModel(VaoManager.loadToVao2d(verticesBottom, uvs), texBottom);
		
		float[] verticesTop = new float[12];
		
		verticesTop[0] = 0;
		verticesTop[1] = topPadding;
		verticesTop[2] = width;
		verticesTop[3] = topPadding;
		verticesTop[4] = width;
		verticesTop[5] = 0;

		verticesTop[6] = width;
		verticesTop[7] = 0;
		verticesTop[8] = 0;
		verticesTop[9] = 0;
		verticesTop[10] = 0;
		verticesTop[11] = topPadding;
		
		modelTop = new Renderable2dModel(VaoManager.loadToVao2d(verticesTop, uvs), texTop);
		
		
		float uvsSides[] = new float[]{0,0,1,0,1,1,1,1,0,1,0,0, 1,0,0,0,0,1, 0,1,1,1,1,0};
		
		float[] verticesSides = new float[24];

		/*
		l
		t+topPadding
		l+leftPadding
		t+topPadding
		l+leftPadding
		b-bottomPadding
		
		l+leftPadding
		b-bottomPadding
		l
		b-bottomPadding
		l
		t+topPadding
		*/
		
		verticesSides[0] = 0;
		verticesSides[1] = topPadding;
		verticesSides[2] = leftPadding;
		verticesSides[3] = topPadding;
		verticesSides[4] = leftPadding;
		verticesSides[5] = height-bottomPadding;

		verticesSides[6] = leftPadding;
		verticesSides[7] = height-bottomPadding;
		verticesSides[8] = 0;
		verticesSides[9] = height-bottomPadding;
		verticesSides[10] = 0;
		verticesSides[11] = topPadding;
		
		//-----------------------------------------
		/*
		r-rightPadding
		t+topPadding
		r
		t+topPadding
		r
		b-bottomPadding
		
		r
		b-bottomPadding
		r-rightPadding
		b-bottomPadding
		r-rightPadding
		t+topPadding
		*/
		
		
		verticesSides[12] = width-rightPadding;
		verticesSides[13] = topPadding;
		verticesSides[14] = width;
		verticesSides[15] = topPadding;
		verticesSides[16] = width;
		verticesSides[17] = height-bottomPadding;

		verticesSides[18] = width;
		verticesSides[19] = height-bottomPadding;
		verticesSides[20] = width-rightPadding;
		verticesSides[21] = height-bottomPadding;
		verticesSides[22] = width-rightPadding;
		verticesSides[23] = topPadding;
		
		modelSides = new Renderable2dModel(VaoManager.loadToVao2d(verticesSides, uvsSides), texSide);
		
		float ctX = width-(topPadding/2)-1;
		float ctY = (topPadding/2);
		float hs = topPadding/2-2;
		
		float[] verticesClose = new float[12];
		verticesClose[0] = ctX-hs;
		verticesClose[1] = ctY-hs;
		verticesClose[2] = ctX+hs;
		verticesClose[3] = ctY-hs;
		verticesClose[4] = ctX+hs;
		verticesClose[5] = ctY+hs;
		verticesClose[6] = ctX+hs;
		verticesClose[7] = ctY+hs;
		verticesClose[8] = ctX-hs;
		verticesClose[9] = ctY+hs;
		verticesClose[10] = ctX-hs;
		verticesClose[11] = ctY-hs;
		
		modelClose = new Renderable2dModel(VaoManager.loadToVao2d(verticesClose, uvs), texClose);
		
	}

	@Override
	public void render() {
		renderInside = false;
		ShaderManager.shader_loadTranslation(getTopLeft());
		modelBack.render();
		ShaderManager.shader_loadTranslation(new Vector2i());
		/*
		float t = getTopLeft().y;
		float l = getTopLeft().x;
		float b = getBottomRight().y;
		float r = getBottomRight().x;
		ShaderManager.shader_bindTextureID(backID, ShaderManager.TEXTURE_COLOR);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (l+leftPadding, t+topPadding);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (r-rightPadding, t+topPadding);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (r-rightPadding, b-bottomPadding);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (l+leftPadding, b-bottomPadding);
		GL11.glEnd();
		*/
		renderInside = true;
		Vector2i mousePos = UserInputUtil.getMousePos();
		for(GUIElement child:getChildrens()) if(!child.isExpandable() || (child.isExpandable() && !child.isExpanded() && !isInElement(child, mousePos)) )child.render();
		for(GUIElement child:getChildrens()) if(child.isExpandable() && (child.isExpanded()|| isInElement(child, mousePos) )) child.render();
		renderInside = false;
		
		ShaderManager.shader_loadTranslation(getTopLeft());
		modelBottom.render();
		
		if(!isFocused()) modelTop.setTexture(texTop);
		else modelTop.setTexture(texTopFocused);
		
		modelTop.render();
		
		modelSides.render();
		
		if(title != null) title.render();

		ShaderManager.shader_loadTranslation(getTopLeft());
		
		if(mouseInClose) ShaderManager.shader_setColorsInverted(true);
		
		modelClose.render();

		if(mouseInClose) ShaderManager.shader_setColorsInverted(false);
		ShaderManager.shader_loadTranslation(new Vector2i());
		
		renderInside = true;
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		ShaderManager.shader_bindTextureID(bottomID, ShaderManager.TEXTURE_COLOR);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (l      , b-bottomPadding);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (r      , b-bottomPadding);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (r      , b);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (l      , b);
		GL11.glEnd();
		if(!isFocused()) ShaderManager.shader_bindTextureID(topID, ShaderManager.TEXTURE_COLOR);
		else ShaderManager.shader_bindTextureID(topFocusedID, ShaderManager.TEXTURE_COLOR);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (l      , t);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (r      , t);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (r      , t+topPadding);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (l      , t+topPadding);
		GL11.glEnd();
		ShaderManager.shader_bindTextureID(sideID, ShaderManager.TEXTURE_COLOR);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (l      , t+topPadding);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (l+leftPadding, t+topPadding);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (l+leftPadding, b-bottomPadding);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (l      , b-bottomPadding);
			
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (r-rightPadding, t+topPadding);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (r      , t+topPadding);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (r      , b-bottomPadding);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (r-rightPadding, b-bottomPadding);
		GL11.glEnd();
		
		if(title != null) title.render();
		
		float ctX = r-(topPadding/2)-1;
		float ctY = t+(topPadding/2);
		float hs = topPadding/2-2;
		ShaderManager.shader_bindTextureID(closeID, ShaderManager.TEXTURE_COLOR);
		if(mouseInClose) ShaderManager.shader_setColorsInverted(true);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2f  (ctX-hs      , ctY-hs);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2f  (ctX+hs, ctY-hs);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2f  (ctX+hs, ctY+hs);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2f  (ctX-hs      , ctY+hs);
		GL11.glEnd();
		if(mouseInClose) ShaderManager.shader_setColorsInverted(false);
		renderInside = true;
		/*
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0      , 0);
			GL11.glVertex2i  (x      , y);
			GL11.glTexCoord2f(1      , 0);
			GL11.glVertex2i  (x+width, y);
			GL11.glTexCoord2f(1      , 1);
			GL11.glVertex2i  (x+width, y+height);
			GL11.glTexCoord2f(0      , 1);
			GL11.glVertex2i  (x      , y+height);
		GL11.glEnd();
		*/
	}
	
	public void setTitle(String title){
		if(this.title != null) this.title.release();
		this.title = new Text2d(title, "tahoma", 250, new Vector2i(5,1), getWidth()-topPadding, false, this);
		this.title.setNumberOfLines(1);
	}

	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	public void reduceLevel(int levelReduction) {
		this.level -= levelReduction;
	}
	
	@Override
	public void resetFocus(){
		super.resetFocus();
		if(focusedChild != null) focusedChild.resetFocus();
	}

	@Override
	public void update() {
		Vector2i mousePos = UserInputUtil.getMousePos();
		
		for(int i=getChildrens().size()-1;i>=0;i--) {
			GUIElement element = getChildrens().get(i);
			if(mouseIn && isInElement(element, mousePos)){
				element.setMouseIn(true);
			}
			else{
				element.setMouseIn(false);
			}
		}
		
		float ctX = getBottomRight().x-(topPadding/2)-1;
		float ctY = getTopLeft().y+(topPadding/2);
		float hs = topPadding/2-2;
		if(!dragged && isFocused() && UserInputUtil.mouseLeftClicked()){
			if(ctX-hs < mousePos.x && ctX+hs > mousePos.x && ctY-hs < mousePos.y && ctY+hs > mousePos.y){
				destroy();
				return;
			}
			if(!dragged && getTopLeft().x < mousePos.x && mousePos.x < getBottomRight().x && getTopLeft().y < mousePos.y && mousePos.y < getTopLeft().y+topPadding){
				dragged = true;
				mouseOldPos = new Vector2i(mousePos);
			}
		}
		if(isFocused() && UserInputUtil.mouseLeftPressed()){
			if(dragged){
				Vector2f mouseD = new Vector2f(mousePos.x - mouseOldPos.x, mousePos.y - mouseOldPos.y);
				translate(mouseD);
				mouseOldPos = new Vector2i(mousePos);
				//LogSystem.out_println("frame moved to "+mouseD.x+";"+mouseD.y);
				return;
			}
		}
		else{
			dragged = false;
		}
		if(isFocused() && UserInputUtil.mouseLeftClicked()){
			boolean focusFound = false;
			for(int i=getChildrens().size()-1;i>=0;i--) {
				GUIElement element = getChildrens().get(i);
				if(element.isExpandable() && element.isExpanded()){
					if(!focusFound && isInElement(element, mousePos)){
						element.setFocus();
						focusFound = true;
					}
					else{
						if(element.isFocused()) element.resetFocus();
					}
				}
			}
			for(int i=getChildrens().size()-1;i>=0;i--) {
				GUIElement element = getChildrens().get(i);
				if(!element.isExpandable() || !element.isExpanded())
				if(!focusFound && isInElement(element, mousePos)){
					element.setFocus();
					focusFound = true;
				}
				else{
					if(element.isFocused()) element.resetFocus();
				}
			}
		}
		if(!isFocused()){
			for(GUIElement element:getChildrens()) element.resetFocus();
		}
		if(mouseIn && ctX-hs < mousePos.x && ctX+hs > mousePos.x && ctY-hs < mousePos.y && ctY+hs > mousePos.y){
			mouseInClose = true;
			return;
		}
		else{
			mouseInClose = false;
		}
		
		for(int i=getChildrens().size()-1;i>=0;i--) {
			GUIElement element = getChildrens().get(i);
			element.update();
		}
	}

	private void translate(Vector2f translation) {
		this.x += translation.x;
		this.y += translation.y;
	}

	private static boolean isInElement(GUIElement element, Vector2i pos) {
		Vector2i tl = element.getTopLeft();
		Vector2i br = element.getBottomRight();
		if(tl.x < pos.x && tl.y < pos.y && br.x > pos.x && br.y > pos.y)
			return true;
		return false;
	}
	
	@Override
	public Vector2i getContainerPos(){
		if(containerPadding == null) return new Vector2i(0,0);
		if(!renderInside) return new Vector2i(x, y);
		return new Vector2i(x+containerPadding.x, y+containerPadding.y);
	}

	public boolean isDragged() {
		return dragged;
	}
	
	@Override
	public void destroy(){
		super.destroy();
		if(title != null) title.release();
	}
	
	@Override
	public boolean isLevelable() {
		return true;
	}

}
