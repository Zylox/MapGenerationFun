package com.zach.mapgen;

import java.util.Random;

public class MapGenerator {

	private static final int TopLeft = 0;
	private static final int TopRight = 1;
	private static final int BottomRight = 2;
	private static final int BottomLeft = 3;
	
	private static final int Top = 0;
	private static final int Right = 1;
	private static final int Bottom = 2;
	private static final int Left = 3;
	
	private static final float EMPTYSENTINEL = -1000;
	
	public static final float HEIGHTSCALINGFACTOR = .5f;
	private static float RANSCALING;

	private float[][] map;
	private Random ran;
	
	
	public float[][] getOpenSimplexHeighMap(float[][] map, long seed){
		OpenSimplexNoise simplex = new OpenSimplexNoise(seed);
		for(int j = 0; j<map.length; j++){
			for(int i = 0; i< map[0].length; i++){
				float value = (float)simplex.eval(i, j);
				map[j][i] = value;
			}
		}
		return map;
	}
	
	public  float[][] genDiamondSquareHeightMap(float[][] map, long seed){
		this.map = map;
		for(float f[] : map){
			for(float x : map[0]){
				x = EMPTYSENTINEL;
			}
		}
		if(!isPowerOfTwo(map[0].length-1) || !isPowerOfTwo(map.length-1)){
			System.out.println("Map must have side length 1+2^x");
			return map;
		}
		ran = new Random(seed);
		
		initCorners(ran);
		IntVector2 initialCorners[] = {new IntVector2(0,map.length-1), 
										new IntVector2(map[0].length-1, map.length-1),
										new IntVector2(map[0].length-1, 0),
										new IntVector2()};
		diamondSquareIterative();
		
		return map;
	}
	
	public float[][] copyMap(float[][] oldMap){
		float newArray[][] = new float[oldMap.length][oldMap[0].length];
		for(int j = oldMap.length-1; j>=0; j--){
			for(int i = oldMap[0].length-1; i>=0;i--){
				newArray[j][i] = oldMap[j][i];
			}
		}
		return newArray;
	}
	
	public float[][] averageSmooth(int iterations){
		
		float[][] newArray = copyMap(map);
		float avgValue = 0;
		
		for(int iter = 0; iter< iterations;iter++){
			for(int j = map.length-1; j>=0; j--){
				for(int i = map[0].length-1; i>=0;i--){
					avgValue = getAvgFromNextTo(new IntVector2(i,j));
					avgValue *=1.4f;
					newArray[j][i] = avgValue;
				}
			}
			map = newArray;
		}
		
		return newArray;
	}
	
	private float getAvgFromNextTo(IntVector2 pos){
		int i = 0;
		float accumulator = 0;
		
		float value = getValueIfExists(pos.x-1, pos.y-1);
		if(value != -1){
			i++;
			accumulator+=value;
		}
		value = getValueIfExists(pos.x, pos.y-1);
		if(value != -1){
			i++;
			accumulator+=value;
		}
		value = getValueIfExists(pos.x+1, pos.y-1);
		if(value != -1){
			i++;
			accumulator+=value;
		}
		value = getValueIfExists(pos.x-1, pos.y);
		if(value != -1){
			i++;
			accumulator+=value;
		}
		value = getValueIfExists(pos.x, pos.y);
		if(value != -1){
			i++;
			accumulator+=value;
		}
		value = getValueIfExists(pos.x+1, pos.y);
		if(value != -1){
			i++;
			accumulator+=value;
		}
		value = getValueIfExists(pos.x-1, pos.y+1);
		if(value != -1){
			i++;
			accumulator+=value;
		}
		value = getValueIfExists(pos.x, pos.y+1);
		if(value != -1){
			i++;
			accumulator+=value;
		}
		value = getValueIfExists(pos.x+1, pos.y+1);
		if(value != -1){
			i++;
			accumulator+=value;
		}
		return accumulator/i;
	}
	
	private float getValueIfExists(int x, int y){
		if(x < 0 || x > map[0].length-1 || y < 0 || y > map.length-1){
		return -1;
		}
		return map[y][x];
	}
	
	private void diamondSquareIterative(){
		int sideLength = map.length-1;
		int iterations = 1;
		IntVector2 corners[] = new IntVector2[4];
		corners[TopLeft] = new IntVector2();
		corners[TopRight] = new IntVector2();
		corners[BottomRight] = new IntVector2();
		corners[BottomLeft] = new IntVector2();
		
		RANSCALING = 1.45f;
		IntVector2 temp = new IntVector2();
		
		while(sideLength > 0){
//		for(int b = 0; b<5; b++){
			
			for(int j = 0; j<iterations; j++){
				for(int i = 0; i <iterations; i++){
					temp.x = i*sideLength+sideLength/2;
					temp.y = j*sideLength+sideLength/2;
					makeDiamond(temp, sideLength/2);
				}
			}
			
			for(int j = 0; j<iterations; j++){
				for(int i = 0; i <iterations; i++){
					corners[TopLeft].x = i*sideLength;
					corners[TopLeft].y = j*sideLength+sideLength;
					corners[TopRight].x = i*sideLength+sideLength;
					corners[TopRight].y = j*sideLength+sideLength;
					corners[BottomRight].x = i*sideLength+sideLength;
					corners[BottomRight].y = j*sideLength;
					corners[BottomLeft].x = i*sideLength;
					corners[BottomLeft].y = j*sideLength;
					reSquareStep(corners, sideLength/2);
				}
			}
			
			iterations *=2;
			sideLength /=2;
			RANSCALING /=1.5f;
		}
	}
	
	private void makeDiamond(IntVector2 mid, int reach){
		IntVector2 topLeft = new IntVector2(mid.x-reach,mid.y+reach);
		IntVector2 topRight = new IntVector2(mid.x+reach,mid.y+reach);
		IntVector2 bottomRight = new IntVector2(mid.x+reach,mid.y-reach);
		IntVector2 bottomLeft = new IntVector2(mid.x-reach,mid.y-reach);
		IntVector2 squareCorners[] = {topLeft, topRight, bottomLeft, bottomLeft};
		map[mid.y][mid.x] = averageWeight(squareCorners, reach);
	}
	
	private void reSquareStep(IntVector2 corners[], int xDiff){
		int yDiff = xDiff;
		IntVector2 leftMidPos = new IntVector2();
		IntVector2 topMidPos = new IntVector2();
		IntVector2 rightMidPos = new IntVector2();
		IntVector2 bottomMidPos = new IntVector2();
		
		//leftMid
		leftMidPos.y = corners[BottomLeft].y+yDiff;
		leftMidPos.x = corners[BottomLeft].x;
		if(map[leftMidPos.y][leftMidPos.x] != EMPTYSENTINEL)
			map[leftMidPos.y][leftMidPos.x] = averageWeight(getDiamondCorners(leftMidPos, xDiff), xDiff);
		
		//topMId
		topMidPos.y = corners[TopLeft].y;
		topMidPos.x = corners[TopLeft].x+xDiff;
		if(map[topMidPos.y][topMidPos.x] != EMPTYSENTINEL)
			map[topMidPos.y][topMidPos.x] = averageWeight(getDiamondCorners(topMidPos, xDiff), xDiff);
		
		//rightMid
		rightMidPos.y = corners[BottomRight].y+yDiff;
		rightMidPos.x = corners[BottomRight].x;
		if(map[rightMidPos.y][rightMidPos.x] != EMPTYSENTINEL)
			map[rightMidPos.y][rightMidPos.x] = averageWeight(getDiamondCorners(rightMidPos, xDiff), xDiff);
		
		//bottomMid
		bottomMidPos.y = corners[BottomLeft].y;
		bottomMidPos.x = corners[BottomLeft].x+xDiff;
		if(map[bottomMidPos.y][bottomMidPos.x] != EMPTYSENTINEL)
			map[bottomMidPos.y][bottomMidPos.x] = averageWeight(getDiamondCorners(bottomMidPos, xDiff), xDiff);
	
	}
	
	private float averageWeight(IntVector2[] cornerPositions, int sideLength){
		float avg = 0;
		int i = 0;
		float weight = -1;
		for(IntVector2 iV : cornerPositions){
			weight = getWeightAt(iV, sideLength);
			if(weight != -1){
				i++;
				avg += weight;
			}
		}
		
		float val = (avg/i) + (ran.nextFloat()-.5f)*RANSCALING;
//		System.out.println(val);
		return val;
	}
	
	private IntVector2[] getDiamondCorners(IntVector2 mid, int reach){
		IntVector2 top = new IntVector2(mid.x,mid.y+reach);
		IntVector2 right = new IntVector2(mid.x+reach,mid.y);
		IntVector2 bottom = new IntVector2(mid.x,mid.y-reach);
		IntVector2 left = new IntVector2(mid.x-reach,mid.y);
		IntVector2 diamondCorners[] = {top,right,bottom,left};
		return diamondCorners;
	}
	
	private float getWeightAt(IntVector2 pos, int sideLength){
		return getWeightAt(pos.x, pos.y, sideLength);
	}
	
	private float getWeightAt(int x, int y, int sideLength){
//		if(x < 0 || x > map[0].length-1 || y < 0 || y > map.length-1){
//			return -1;
//		}
		
		if(x<0){
			return map[y][sideLength];
		}else if(x > map[0].length - 1){
			return map[y][map[0].length - 1 - sideLength];
		}
		
		if(y< 0){
			return map[sideLength][x];
		}else if(y>map.length-1){
			return map[map.length-1-sideLength][x];
		}
		return map[y][x];
	}
	
	private void initCorners(Random ran){
		System.out.println(map.length);
		map[0][0]                      = ran.nextFloat() * HEIGHTSCALINGFACTOR;
		map[map.length-1][0]             = ran.nextFloat() * HEIGHTSCALINGFACTOR;
		map[0][map[0].length-1] 		   = ran.nextFloat() * HEIGHTSCALINGFACTOR;
		map[map.length-1][map[0].length-1] = ran.nextFloat() * HEIGHTSCALINGFACTOR;
	}
	
	
	private static boolean isPowerOfTwo(int number){
		return (number & (number - 1)) == 0;
	}
	
	
	private class IntVector2{
		
		public int x;
		public int y;
		
		public IntVector2(){
			this(0,0);
		}
		
		public IntVector2(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
}
