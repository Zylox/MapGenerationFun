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

public class MapGenerationCore extends Game {
	SpriteBatch batch;
	Texture img;
	Pixmap pix;
	
	private static final int sideLength = (int) (1+Math.pow(2, 10));
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		generateNewMap();
	}

	private void generateNewMap(){
		MapGenerator mapgen = new MapGenerator();
		float[][] map = new float[sideLength][sideLength];
//		map = mapgen.genDiamondSquareHeightMap(map, System.currentTimeMillis());
		map = mapgen.getOpenSimplexHeighMap(map, System.currentTimeMillis());
		pix = genGreyscaleMap(map);
		img = new Texture(pix);
		
		
	}
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(Gdx.input.isKeyPressed(Input.Keys.Q)){
			generateNewMap();
		}
		
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	private static final Format FORMAT = Pixmap.Format.RGBA4444;
	private Pixmap genGreyscaleMap(float[][] map){
//		Gdx2DPixmap pix = new Gdx2DPixmap(map[0].length, map.length, FORMAT);
		Pixmap pix = new Pixmap(map[0].length, map.length, FORMAT);
		float mapValue = 0;
		for(int j = 0; j < map.length; j++){
			for(int i =0; i < map[0].length; i++){
				mapValue = map[j][i];
//				System.out.println(mapValue);
				pix.setColor(new Color(mapValue,mapValue,mapValue,1));
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
