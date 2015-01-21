package com.zach.mapgen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class MapGenerationCore extends Game {
	SpriteBatch batch;
	Image img;
	Pixmap pix;
	
	float[][] map;
	MapGenerator mapgen;
	
	Stage stage;
	private static final int magicNumber = 1;
	private static final int sideLength = (int) (1+Math.pow(2, 10-magicNumber));
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		generateNewMap();
		stage = new Stage();
		Table table = new Table();
		table.setFillParent(true);
		//stage.addActor(table);
		//table.add(img).expand();
		stage.addActor(img);
	}

	private void generateNewMap(){
		mapgen = new MapGenerator();
		map = new float[sideLength][sideLength];
		map = mapgen.genDiamondSquareHeightMap(map, System.currentTimeMillis());
		
//		map = mapgen.getOpenSimplexHeighMap(map, System.currentTimeMillis());
		generateNewPicture();
	}
	
	private void generateNewPicture(){
		pix = genGreyscaleMap(map);
		img = new Image(new Texture(pix));
		img.scaleBy(magicNumber);
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(Gdx.input.isKeyPressed(Input.Keys.Q)){
			generateNewMap();
		}else if(Gdx.input.justTouched() || Gdx.input.isKeyPressed(Input.Keys.W)){
			map = mapgen.averageSmooth(1);
			generateNewPicture();
		}
		batch.begin();
		img.draw(batch, 1);
		batch.end();
//		stage.draw();
	}
	
	private static final Format FORMAT = Pixmap.Format.RGBA8888;
	private Pixmap genGreyscaleMap(float[][] map){
//		Gdx2DPixmap pix = new Gdx2DPixmap(map[0].length, map.length, FORMAT);
		Pixmap pix = new Pixmap(map[0].length, map.length, FORMAT);
		float mapValue = 0;
		for(int j = 0; j < map.length; j++){
			for(int i =0; i < map[0].length; i++){
				mapValue = map[j][i];
//				System.out.println(mapValue);
//				pix.setColor(new Color((int)Math.pow(mapValue,mapValue),mapValue*mapValue,(int)Math.exp(mapValue),(float)Math.sin(mapValue)));
//				pix.setColor(new Color(1-(float)Math.sin(mapValue), 1-(float)Math.cos(mapValue), mapValue, 1));
				pix.setColor(new Color(mapValue, mapValue, mapValue, 1));
				pix.drawPixel(i, j);
			}
		}
		return pix;
	}
	
	@Override
	public void dispose(){
		super.dispose();
		pix.dispose();
	}
}
