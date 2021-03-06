package slaynash.sgengine.world3d.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import slaynash.sgengine.Configuration;
import slaynash.sgengine.LogSystem;
import slaynash.sgengine.entities.Entity;
import slaynash.sgengine.entities.EntityManager;
import slaynash.sgengine.world3d.Model3dWorld;
import slaynash.sgengine.world3d.entities.InteractableObject;
import slaynash.sgengine.world3d.entities.Interaction;

public class WorldLoader {

	private static boolean clean = true;
	
	private static boolean error = false;
	private static String errorMessage = "";
	
	private static String worldVersion = "0.0";
	private static String worldName = "null";
	private static String worldCreator = "unknown";
	private static Vector3f worldSpawn = new Vector3f(0,0,0);
	private static float scaleFactor = 1;

	private static List<Model3dWorld> worldparts = new ArrayList<Model3dWorld>();
	
	
	
	public static void loadMap(String mapPath){
		if(!clean) reset();
		clean = false;
		LogSystem.out_println("[WorldLoader] Start loading 3d map \""+mapPath+"\"...");
		File mapFile = new File(mapPath);
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(mapFile));
		} catch (FileNotFoundException e) {error = true; errorMessage = e.getMessage(); return;}
		
		try {
			boolean found = false;
			String ln = "";
			String command = "";
			String args = "";
				
			while(!found && (ln=reader.readLine()) != null) {
				if(ln.split(" ", 2)[0].equals("MapVersion")){
					LogSystem.out_println("[WorldLoader] Map version: "+ln.split(" ", 2)[1]);
					worldVersion = ln.split(" ", 2)[1];
					found = true;
				}
			}
			if(worldVersion.equals("0.0")){
				error = true; errorMessage = "Bad map file ! (version not found)"; reader.close(); return;
			}
			else if(worldVersion.equals("1.3"))
				readMap1_3(ln, command, args, reader);
			else if(worldVersion.equals("1.4"))
				readMap1_4(ln, command, args, reader);
			else if(worldVersion.equals("1.5"))
				readMap1_5(ln, command, args, reader);
			else if(worldVersion.equals("1.6_unofficial_mixed1.4"))
				readMap1_6_unofficial_mixed1_4(ln, command, args, reader);
			else{
				error = true; errorMessage = "Version not readable. Version: "+worldVersion; reader.close(); return;
			}
			
			
			command = null;
			args = null;
		} catch (IOException e) {error = true; errorMessage = e.getMessage(); return;}
		LogSystem.out_println("[WorldLoader] Load finished. map properties:");
		LogSystem.out_println("[WorldLoader] ------------------------------");
		LogSystem.out_println("[WorldLoader] Map \""+worldName+"\" for loader v"+worldVersion+", created by \""+worldCreator+"\".");
		LogSystem.out_println("[WorldLoader] World spawn placed at "+worldSpawn.x+" "+worldSpawn.y+" "+worldSpawn.z);
		int totalTriangles = 0;
		for(Model3dWorld wp:worldparts) {
			totalTriangles += wp.getFaces().length;
		}
		LogSystem.out_println("[WorldLoader] "+worldparts.size()+" world parts, with a total of "+totalTriangles+" faces (triangles).");
		LogSystem.out_println("[WorldLoader] ------------------------------");
	}
	
	private static void readMap1_3(String ln, String command, String args, BufferedReader reader) {
		
		int worldObjectId = 0;
		int line=1;
		try {
			while((ln=reader.readLine()) != null) {line++;
				ln = ln.trim();
				if(ln.startsWith("#")) continue;
				command = ln.split(" ", 2)[0].toLowerCase();
				if(command.equals("")) continue;
				args = ln.split(" ", 2)[1];
				if(command.equals("spawn")) worldSpawn.set(Float.parseFloat(args.split(" ")[0]), Float.parseFloat(args.split(" ")[1]), Float.parseFloat(args.split(" ")[2]));
				else if(command.equals("name")) worldName = args;
				else if(command.equals("creator")) worldCreator = args;
				else if(command.equals("worldpart")){
					String[] ag = args.split(" ");
					TriangleFace[] faces = new TriangleFace[Integer.parseInt(ag[0])];
					float[] vs = null;
					float[] uvs = null;
					String texC = "";
					String texN = "";
					String texS = "";
					float sf = 0;
					
					int vn = 0;
					
					int fn = 0;
					while((ln=reader.readLine()) != null){line++;
						ln = ln.trim();
						if(ln.startsWith("#")) continue;
						command = ln.split(" ", 2)[0].toLowerCase();
						if(command.equals("")) continue;
						ag = ln.split(" ");
						if(command.equals("endworldpart")){
							Model3dWorld m3dw = new Model3dWorld(faces);
							worldparts.add(m3dw);
							LogSystem.out_println("m3dw created with "+faces.length+" faces !");
							break;
						}
						if(command.equals("face")){
							vs = new float[Integer.parseInt(ag[1])*3];
							uvs = new float[Integer.parseInt(ag[1])*2];
						}
						else if(command.equals("endface")){
							faces[fn] = new TriangleFace(vs, uvs, texC, texN, texS, sf);
							texC = "";
							texN = "";
							texS = "";
							sf = 0;
							fn++;
							vn = 0;
						}
						else if(command.equals("v")){//vertices*3 normals*3 uvs*2
							 vs[vn*3+0] = Float.parseFloat(ag[1])*scaleFactor;
							 vs[vn*3+1] = Float.parseFloat(ag[2])*scaleFactor;
							 vs[vn*3+2] = Float.parseFloat(ag[3])*scaleFactor;
							
							uvs[vn*2+0] = Float.parseFloat(ag[7]);
							uvs[vn*2+1] = Float.parseFloat(ag[8]);
							vn++;
							
						}
						else if(command.equals("texc")){//color texture
							texC = ln.split(" ", 2)[1];
						}
						else if(command.equals("texn")){//normal texture
							texN = ln.split(" ", 2)[1];
						}
						else if(command.equals("texs")){//specular texture
							texS = ln.split(" ", 2)[1];
						}
						else if(command.equals("sf")){//specular factor
							sf = Float.parseFloat(ln.split(" ", 2)[1]);
						}
						else{
							LogSystem.out_println("Unknown worldpart line: "+ln);
						}
					}
				}
				else if(command.equals("model3d")){/*Not implemented*/}
				else if(command.equals("pointlight")){
					String[] ags = args.split(" ");
					Ent_PointLight l = new Ent_PointLight("worldentity_"+(worldObjectId+1),
							Float.parseFloat(ags[0])*scaleFactor, Float.parseFloat(ags[1])*scaleFactor, Float.parseFloat(ags[2])*scaleFactor,
							Float.parseFloat(ags[3]), Float.parseFloat(ags[4]), Float.parseFloat(ags[5]),
							Float.parseFloat(ags[6]), Float.parseFloat(ags[7]), Float.parseFloat(ags[8])
					);
					EntityManager.addEntity(l);
				}
			}
			//OLD: PlayerCharacter.instance.warp(worldSpawn);
			Configuration.getPlayerCharacter().warp(worldSpawn);
		}
		catch(Exception e) {
			error = true;
			errorMessage = "Error while reading line "+line+": "+e.getClass()+": "+e.getLocalizedMessage();
		}
	}
	
	private static void readMap1_4(String ln, String command, String args, BufferedReader reader) {
		
		int worldObjectId = 0;
		float dx=0, dy=0, dz=0;
		
		int line=1;
		try {
			while((ln=reader.readLine()) != null) {line++;
				ln = ln.trim();
				if(ln.startsWith("#")) continue;
				command = ln.split(" ", 2)[0].toLowerCase();
				if(command.equals("")) continue;
				args = ln.split(" ", 2)[1];
				if(command.equals("spawn")) worldSpawn.set(Float.parseFloat(args.split(" ")[0]), Float.parseFloat(args.split(" ")[1]), Float.parseFloat(args.split(" ")[2]));
				else if(command.equals("name")) worldName = args;
				else if(command.equals("creator")) worldCreator = args;
				if(command.equals("mapdisplacement")){
					String[] t = args.split(" ", 3);
					dx = Float.parseFloat(t[0]);
					dy = Float.parseFloat(t[1]);
					dz = Float.parseFloat(t[2]);
				}
				else if(command.equals("worldpart")){
					String[] ag = args.split(" ");
					TriangleFace[] faces = new TriangleFace[Integer.parseInt(ag[0])];
					float[] vs = null;
					float[] uvs = null;
					String texC = "";
					String texN = "";
					String texS = "";
					float sf = 0;
					
					int vn = 0;
					
					int fn = 0;
					while((ln=reader.readLine()) != null){line++;
						ln = ln.trim();
						if(ln.startsWith("#")) continue;
						command = ln.split(" ", 2)[0].toLowerCase();
						if(command.equals("")) continue;
						ag = ln.split(" ");
						if(command.equals("endworldpart")){
							Model3dWorld m3dw = new Model3dWorld(faces);
							worldparts.add(m3dw);
							break;
						}
						if(command.equals("face")){
							vs = new float[Integer.parseInt(ag[1])*3];
							uvs = new float[Integer.parseInt(ag[1])*2];
						}
						else if(command.equals("endface")){
							faces[fn] = new TriangleFace(vs, uvs, texC, texN, texS, sf);
							texC = "";
							texN = "";
							texS = "";
							sf = 0;
							fn++;
							vn = 0;
						}
						else if(command.equals("v")){//vertices*3 normals*3 uvs*2
							 vs[vn*3+0] = (Float.parseFloat(ag[1])+dx)*scaleFactor;
							 vs[vn*3+1] = (Float.parseFloat(ag[2])+dy)*scaleFactor;
							 vs[vn*3+2] = (Float.parseFloat(ag[3])+dz)*scaleFactor;
							
							uvs[vn*2+0] = Float.parseFloat(ag[7]);
							uvs[vn*2+1] = Float.parseFloat(ag[8]);
							vn++;
							
						}
						else if(command.equals("texc")){//color texture
							texC = ln.split(" ", 2)[1];
						}
						else if(command.equals("texn")){//normal texture
							texN = ln.split(" ", 2)[1];
						}
						else if(command.equals("texs")){//specular texture
							texS = ln.split(" ", 2)[1];
						}
						else if(command.equals("sf")){//specular factor
							sf = Float.parseFloat(ln.split(" ", 2)[1]);
						}
						else{
							LogSystem.out_println("[WorldLoader] Unknown worldpart line: "+ln);
						}
					}
				}
				else if(command.equals("model3d")){/*Not implemented*/}
				else if(command.equals("pointlight")){
					String[] ags = args.split(" ");
					Ent_PointLight l = new Ent_PointLight("worldentity_"+(worldObjectId+1),
							(Float.parseFloat(ags[0])+dx)*scaleFactor, (Float.parseFloat(ags[1])+dy)*scaleFactor, (Float.parseFloat(ags[2])+dz)*scaleFactor,
							Float.parseFloat(ags[3]), Float.parseFloat(ags[4]), Float.parseFloat(ags[5]),
							Float.parseFloat(ags[6]), Float.parseFloat(ags[7]), Float.parseFloat(ags[8])
					);
					EntityManager.addEntity(l);
				}
			}
			Configuration.getPlayerCharacter().warp(worldSpawn);
		}
		catch(Exception e) {
			error = true;
			errorMessage = "Error while reading line "+line+": "+e.getClass()+": "+e.getLocalizedMessage();
		}
	}
	
	private static void readMap1_5(String ln, String command, String args, BufferedReader reader) {
		
		int worldObjectId = 0;
		float dx=0, dy=0, dz=0;
		
		int line=1;
		try {
			while((ln=reader.readLine()) != null) {line++;
				ln = ln.trim();
				if(ln.startsWith("#")) continue;
				command = ln.split(" ", 2)[0].toLowerCase();
				if(command.equals("")) continue;
				//LogSystem.out_println("ln="+ln);
				args = ln.split(" ", 2)[1];
				if(command.equals("worldspawn")) worldSpawn.set(Float.parseFloat(args.split(" ")[0])*scaleFactor, Float.parseFloat(args.split(" ")[1])*scaleFactor, Float.parseFloat(args.split(" ")[2])*scaleFactor);
				else if(command.equals("name")) worldName = args;
				else if(command.equals("creator")) worldCreator = args;
				if(command.equals("mapdisplacement")){
					String[] t = args.split(" ", 3);
					dx = Float.parseFloat(t[0]);
					dy = Float.parseFloat(t[1]);
					dz = Float.parseFloat(t[2]);
				}
				else if(command.equals("worldpart")){
					String[] ag = args.split(" ");
					TriangleFace[] faces = new TriangleFace[Integer.parseInt(ag[0])];
					float[] vs = null;
					float[] uvs = null;
					String texC = "";
					String texN = "";
					String texS = "";
					float sf = 0;
					
					int vn = 0;
					
					int fn = 0;
					while((ln=reader.readLine()) != null){line++;
						ln = ln.trim();
						if(ln.startsWith("#")) continue;
						command = ln.split(" ", 2)[0].toLowerCase();
						if(command.equals("")) continue;
						ag = ln.split(" ");
						if(command.equals("endworldpart")){
							Model3dWorld m3dw = new Model3dWorld(faces);
							worldparts.add(m3dw);
							break;
						}
						if(command.equals("face")){
							vs = new float[Integer.parseInt(ag[1])*3];
							uvs = new float[Integer.parseInt(ag[1])*2];
						}
						else if(command.equals("endface")){
							faces[fn] = new TriangleFace(vs, uvs, texC, texN, texS, sf);
							texC = "";
							texN = "";
							texS = "";
							sf = 0;
							fn++;
							vn = 0;
						}
						else if(command.equals("v")){//vertices*3 uvs*2
							 vs[vn*3+0] = (Float.parseFloat(ag[1])+dx)*scaleFactor;
							 vs[vn*3+1] = (Float.parseFloat(ag[2])+dy)*scaleFactor;
							 vs[vn*3+2] = (Float.parseFloat(ag[3])+dz)*scaleFactor;
							
							uvs[vn*2+0] = Float.parseFloat(ag[4]);
							uvs[vn*2+1] = Float.parseFloat(ag[5]);
							vn++;
							
						}
						else if(command.equals("texc")){//color texture
							texC = ln.split(" ", 2)[1];
						}
						else if(command.equals("texn")){//normal texture
							texN = ln.split(" ", 2)[1];
						}
						else if(command.equals("texs")){//specular texture
							texS = ln.split(" ", 2)[1];
						}
						else if(command.equals("sf")){//specular factor
							sf = Float.parseFloat(ln.split(" ", 2)[1]);
						}
						else{
							LogSystem.out_println("[WorldLoader] Unknown worldpart line: "+ln);
						}
					}
				}
				else if(command.equals("model3d")){/*Not implemented*/}
				else if(command.equals("pointlight")){
					String[] ags = args.split(" ");
					Ent_PointLight l = new Ent_PointLight("worldentity_"+(worldObjectId+1),
							(Float.parseFloat(ags[4])+dx)*scaleFactor, (Float.parseFloat(ags[5])+dy)*scaleFactor, (Float.parseFloat(ags[6])+dz)*scaleFactor,
							Float.parseFloat(ags[1]), Float.parseFloat(ags[2]), Float.parseFloat(ags[3]),
							Float.parseFloat(ags[7]), Float.parseFloat(ags[8]), Float.parseFloat(ags[9])
					);
					EntityManager.addEntity(l);
				}
			}
			Configuration.getPlayerCharacter().warp(worldSpawn);
		}
		catch(Exception e) {
			error = true;
			errorMessage = "Error while reading line "+line+": "+e.getClass()+": "+e.getLocalizedMessage();
		}
	}
	

	
	private static void readMap1_6_unofficial_mixed1_4(String ln, String command, String args, BufferedReader reader) {
		float dx=0, dy=0, dz=0;
		
		int line=1;
		try {
			while((ln=reader.readLine()) != null) {line++;
				ln = ln.trim();
				if(ln.startsWith("#")) continue;
				command = ln.split(" ", 2)[0].toLowerCase();
				if(command.equals("")) continue;
				args = ln.split(" ", 2)[1];
				if(command.equals("spawn")) worldSpawn.set(Float.parseFloat(args.split(" ")[0]), Float.parseFloat(args.split(" ")[1]), Float.parseFloat(args.split(" ")[2]));
				else if(command.equals("name")) worldName = args;
				else if(command.equals("creator")) worldCreator = args;
				if(command.equals("mapdisplacement")){
					String[] t = args.split(" ", 3);
					dx = Float.parseFloat(t[0]);
					dy = Float.parseFloat(t[1]);
					dz = Float.parseFloat(t[2]);
				}
				else if(command.equals("worldpart")){
					String[] ag = args.split(" ");
					TriangleFace[] faces = new TriangleFace[Integer.parseInt(ag[0])];
					float[] vs = null;
					float[] uvs = null;
					String texC = "";
					String texN = "";
					String texS = "";
					float sf = 0;
					
					int vn = 0;
					
					int fn = 0;
					while((ln=reader.readLine()) != null){line++;
						ln = ln.trim();
						if(ln.startsWith("#")) continue;
						command = ln.split(" ", 2)[0].toLowerCase();
						if(command.equals("")) continue;
						ag = ln.split(" ");
						if(command.equals("endworldpart")){
							Model3dWorld m3dw = new Model3dWorld(faces);
							worldparts.add(m3dw);
							break;
						}
						if(command.equals("face")){
							vs = new float[Integer.parseInt(ag[1])*3];
							uvs = new float[Integer.parseInt(ag[1])*2];
						}
						else if(command.equals("endface")){
							faces[fn] = new TriangleFace(vs, uvs, texC, texN, texS, sf);
							texC = "";
							texN = "";
							texS = "";
							sf = 0;
							fn++;
							vn = 0;
						}
						else if(command.equals("v")){//vertices*3 normals*3 uvs*2
							 vs[vn*3+0] = (Float.parseFloat(ag[1])+dx)*scaleFactor;
							 vs[vn*3+1] = (Float.parseFloat(ag[2])+dy)*scaleFactor;
							 vs[vn*3+2] = (Float.parseFloat(ag[3])+dz)*scaleFactor;
							
							uvs[vn*2+0] = Float.parseFloat(ag[7]);
							uvs[vn*2+1] = Float.parseFloat(ag[8]);
							vn++;
							
						}
						else if(command.equals("texc")){//color texture
							texC = ln.split(" ", 2)[1];
						}
						else if(command.equals("texn")){//normal texture
							texN = ln.split(" ", 2)[1];
						}
						else if(command.equals("texs")){//specular texture
							texS = ln.split(" ", 2)[1];
						}
						else if(command.equals("sf")){//specular factor
							sf = Float.parseFloat(ln.split(" ", 2)[1]);
						}
						else{
							LogSystem.out_println("[WorldLoader] Unknown worldpart line: "+ln);
						}
					}
				}
				else if(command.equals("model3d")){/*TODO model loading*/}
				else if(command.equals("entity")){
					String id = args.split(" ", 2)[0];
					String entity = args.split(" ", 2)[1];
					List<Interaction> interactions = new ArrayList<Interaction>();
					Map<String, String> entityParameters = new HashMap<String, String>();
					Vector3f pos = new Vector3f(0,0,0);
					Vector3f ang = new Vector3f(0,0,0);
					
					while((ln=reader.readLine()) != null){line++;
						ln = ln.trim();
						if(ln.startsWith("#")) continue;
						command = ln.split(" ", 2)[0].toLowerCase();
						if(command.equals("")) continue;
						if(command.equals("endentity")) break;
						if(command.equals("pos")) {
							pos.x = Float.parseFloat(ln.split(" ", 4)[1]);
							pos.y = Float.parseFloat(ln.split(" ", 4)[2]);
							pos.z = Float.parseFloat(ln.split(" ", 4)[3]);
						}
						else if(command.equals("ang")) {
							ang.x = Float.parseFloat(ln.split(" ", 4)[1]);
							ang.y = Float.parseFloat(ln.split(" ", 4)[2]);
							ang.z = Float.parseFloat(ln.split(" ", 4)[3]);
						}
						else if(command.equals("interaction")) {
							String[] intArgs = null;
							if(ln.split(" ", 5).length >= 5 && !ln.split(" ", 5)[4].equals("")) intArgs = ln.split(" ", 5)[4].split(" ");
							interactions.add(new Interaction(ln.split(" ", 5)[1], ln.split(" ", 5)[2], ln.split(" ", 5)[3], intArgs));
						}
						else {
							entityParameters.put(command, ln.split(" ", 2)[1]);
						}
					}
					Entity entityC = EntityManager.createEntity(id, entity, pos.x, pos.y, pos.z, ang.x, ang.y, ang.z, entityParameters);
					if(entityC instanceof InteractableObject) ((InteractableObject) entityC).addInteractions(interactions);
				}
			}
			Configuration.getPlayerCharacter().warp(worldSpawn);
		}
		catch(Exception e) {
			error = true;
			errorMessage = "Error while reading line "+line+" \""+ln+"\": "+e.getClass()+": "+e.getLocalizedMessage();
			e.printStackTrace();
		}
	}
	
	public static String getError(){
		String error = errorMessage;
		errorMessage = "";
		return error;
	}
	
	public static boolean isErrored(){
		return error;
	}
	
	public static void reset(){
		EntityManager.clear();
		worldVersion = "0.0";
		worldName = "null";
		worldCreator = "unknown";
		worldSpawn.x = 0;
		worldSpawn.y = 0;
		worldSpawn.z = 0;
		error = false;
		errorMessage = "";
		scaleFactor = 1;
		clean = true;
	}
	
	
	
	public static float getScaleFactor() {
		return scaleFactor;
	}

	public static void setScaleFactor(float scaleFactor) {
		WorldLoader.scaleFactor = scaleFactor;
	}

	public static String getWorldVersion() {
		return worldVersion;
	}

	public static String getWorldName() {
		return worldName;
	}

	public static String getWorldCreator() {
		return worldCreator;
	}

	public static Vector3f getWorldSpawn() {
		return worldSpawn;
	}
	
	public static void render() {
		synchronized (worldparts) {
			for(Model3dWorld m:worldparts) m.render();
		}
	}
	
	public static void renderVR(int eye) {
		synchronized (worldparts) {
			for(Model3dWorld m:worldparts) m.renderVR(eye);
		}
	}
	
	public static List<Model3dWorld> getWorldParts() {
		return worldparts;
	}
}
