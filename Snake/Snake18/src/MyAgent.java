import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import za.ac.wits.snake.DevelopmentAgent;
import za.ac.wits.snake.Grid;
import za.ac.wits.snake.utils.Point;

import static java.lang.Math.abs;
import static java.lang.Math.random;

public class MyAgent extends DevelopmentAgent {

    public static void main(String args[]) {
        MyAgent agent = new MyAgent();
        MyAgent.start(agent, args);
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String initString = br.readLine();
            String[] temp = initString.split(" "); //Used to get the size of the grid
            int nSnakes = Integer.parseInt(temp[0]);  // Number of snakes
            int gridx= Integer.parseInt(temp[1]); //Grid X length
            int gridy = Integer.parseInt(temp[2]); //Grid Y length
            int gamemode =  Integer.parseInt(temp[3]); //GameMode


            int risk = 1;

            while (true) {

                int[][] Grid1 = new int[gridx][gridy]; //Get the Grid Size (Represents the blocked grid
                for(int i = 0; i < gridx ; i++) { // Intialize empty space to 0
                    for(int j = 0 ; j <gridy;j++) {
                        Grid1[i][j]= 0;
                    }
                }

                String line = br.readLine();
                if (line.contains("Game Over")) {
                    break;
                }
                ArrayList<String> heads = new ArrayList<String>();
                ArrayList<String> toDraw = new ArrayList<String>();
                String apple1 = line; // apple position, (Extract the co ordinate using split function
                String [] xy =  (line.split(" "));
                int applex = Integer.parseInt(xy[0]);
                int appley= Integer.parseInt(xy[1]); // Extracted the Co-ordinates
                //do stuff with apples


                for (int zombie=0; zombie<6; zombie++) {
                    String zombieLine=br.readLine();  // Using Draw Snake function
                    String[] zom = zombieLine.split(" ");
                    heads.add(zom[0]);

                    drawSnake(zombieLine, nSnakes + 1,Grid1, risk, true); // Adds Zombie Snakes to the Grid
                    toDraw.add(zombieLine);
                }
                int Snakeheadx = 0;
                int Snakeheady = 0;
                int Snaketailx = 0;
                int Snaketaily = 0;
                int myLength = 0;

                int mySnakeNum = Integer.parseInt(br.readLine()) + 1; // snake number
                for (int i = 1; i < nSnakes + 1; i++) {  // draw out all the snakes  (put Draw Snake and DrawLine)
                    //DrawSnake,DrawLine
                    String snakeLine = br.readLine(); // Nested Method in Draw Snake
                    String[] SnakeLineVariable = snakeLine.split(" ");  //Use split function to separate the values to get the snake co-ordinates, alive


                    String Alive = SnakeLineVariable[0];
                    if(Alive.equals("alive")) {
                        if (i != mySnakeNum) {
                            heads.add(SnakeLineVariable[3]);
                        }
                        Integer Length =  Integer.parseInt(SnakeLineVariable[1]);
                        Integer Kills = Integer.parseInt(SnakeLineVariable[2]);
                        String NumSnakeLine = "";
                        for (int j = 0; j < SnakeLineVariable.length-3; j++) {
                            NumSnakeLine = NumSnakeLine + SnakeLineVariable[3 + j]+ " "; // Used to Get string of variables
                        }

                        if (i == mySnakeNum) {
                            myLength = Length;

                            if (Length <= 10) {
                                risk = 1;
                            } else if (Length > 10) {
                                risk = 2;
                            }

                            String SHead []= SnakeLineVariable[3].split(",");
                            String STail []= SnakeLineVariable[SnakeLineVariable.length - 1].split(",");
                            Snakeheadx = Integer.parseInt(SHead[0]);
                            Snakeheady = Integer.parseInt(SHead[1]);
                            Snaketailx = Integer.parseInt(STail[0]);
                            Snaketaily = Integer.parseInt(STail[1]);

                            toDraw.add(NumSnakeLine);

                            drawSnake(NumSnakeLine, i,Grid1, 0, false);

                        }
                        else {
                            toDraw.add(NumSnakeLine);

                            drawSnake(NumSnakeLine, i, Grid1, risk, false);

                        }

                    }
                    else if ((Alive.equals("dead")) && (i == mySnakeNum)) {
                        // Snake is Dead
                        continue;
                    }
                    //do stuff with other snakes
                }
                //finished reading, calculate move: A* Algorithm

                int safestx = Safest(Snakeheadx, Snakeheady, gridx, gridy, Grid1, nSnakes)[0];
                int safesty = Safest(Snakeheadx, Snakeheady, gridx, gridy, Grid1, nSnakes)[1];


                int myPath = getPathSize(Snakeheadx,Snakeheady,applex,appley, gridx, gridy, Grid1);
                if ((myPath == 0)&&(getPathSize(Snakeheadx,Snakeheady,safestx,safesty, gridx, gridy, Grid1) == 0)){
                    lastResort(Snakeheadx,Snakeheady,safestx,safesty,gridx,gridy,toDraw,nSnakes, mySnakeNum, Snaketailx, Snaketaily);
                    continue;
                }


                if (worthIt(applex, appley, Snakeheadx, Snakeheady, gridx, gridy, Grid1, heads, toDraw, myLength)) {

                    if ((myLength >= 15) && (PossibleHeadon(Grid1, applex, appley, nSnakes, mySnakeNum, heads) == true)) {
                        Travel(Snakeheadx, Snakeheady, safestx, safesty, gridx, gridy, Grid1);
                    } else {
                        Travel(Snakeheadx, Snakeheady, applex, appley, gridx, gridy, Grid1);
                    }
                }
                else {

                    if (getPathSize(Snakeheadx,Snakeheady,safestx, safesty,gridx,gridy, Grid1) >= 6) {

                        Travel(Snakeheadx, Snakeheady, safestx, safesty, gridx, gridy, Grid1);
                    }
                    else {
                        if (getPathSize(Snakeheadx, Snakeheady, Snaketailx, Snaketaily, gridx, gridy, Grid1) >= 4) {
                            Grid1[Snaketailx][Snaketaily] = 0;
                            Travel(Snakeheadx, Snakeheady, Snaketailx, Snaketaily, gridx, gridy, Grid1);
                        }
                        else{

                            Travel(Snakeheadx, Snakeheady, applex, appley, gridx, gridy, Grid1);

                        }

                    }

                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean PossibleHeadon(int[][] m, int x, int y, int nSnakes, int mySnakeNum, ArrayList<String> heads) {
        int headx = 0;
        int heady = 0;
        for (int i = 0; i < heads.size(); i++) {
            String cords []= heads.get(i).split(",");

            headx = Integer.parseInt(cords[0]);
            heady = Integer.parseInt(cords[1]);


            if ((x - 1 == headx)&& (y == heady)){
                return true;
            }

            if ((x + 1 == headx)&& (y == heady)){
                return true;
            }

            if ((x == headx)&& (y - 1 == heady)){
                return true;
            }

            if ((x == headx)&& (y + 1 == heady)){
                return true;
            }
        }

        return  false;

    }

    public static int[] Safest(int Snakeheadx,int Snakeheady, int gridx, int gridy, int[][] Grid1, int nSnakes) {

        int quad1 = 0;
        for(int i = 0; i < 25 ; i++) {
            for(int j = 0 ; j <25;j++) {

                if  (Grid1[i][j] == nSnakes + 1){
                    quad1 += 2;
                }
                else if (Grid1[i][j] != 0){
                    quad1 += 1;
                }
            }
        }


        int quad2 = 0;
        for(int i = 25; i < 50 ; i++) {
            for(int j = 0 ; j < 25;j++) {
                if  (Grid1[i][j] == nSnakes + 1){
                    quad2 += 2;
                }
                else if (Grid1[i][j] != 0){
                    quad2 += 1;
                }
            }
        }


        int quad3 = 0;
        for(int i = 0; i < 25 ; i++) {
            for(int j = 25 ; j < 50;j++) {
                if  (Grid1[i][j] == nSnakes + 1){
                    quad3 += 2;
                }
                else if (Grid1[i][j] != 0){
                    quad3 += 1;
                }
            }
        }


        int quad4 = 0;
        for(int i = 25; i < 50 ; i++) {
            for(int j = 25 ; j < 50;j++) {
                if  (Grid1[i][j] == nSnakes + 1){
                    quad4 += 2;
                }
                else if (Grid1[i][j] != 0){
                    quad4 += 1;
                }
            }
        }

        int min = Math.min(Math.min(quad1,quad2), Math.min(quad3, quad4));
        int[] cords = new int[2];

        if (min == quad1){
            cords[0] = 12;
            cords[1] = 12;

        }
        if (min == quad2){
            cords[0] = 37;
            cords[1] = 12;
        }
        if (min == quad3){
            cords[0] = 12;
            cords[1] = 37;
        }
        if (min == quad4){
            cords[0] = 37;
            cords[1] = 37;
        }

        while  ((cords[0] < 0)||(cords[0] > 49)||(cords[1] < 0)||(cords[1] > 49)||(Grid1[cords[0]][cords[1]] != 0)){
            cords[0] += ThreadLocalRandom.current().nextInt(-5, 5 + 1);
            cords[1] += ThreadLocalRandom.current().nextInt(-5, 5 + 1);
        }

        return cords;

    }


    public static void Travel(int Snakeheadx, int Snakeheady, int endx, int endy, int gridx, int gridy, int[][] Grid1) {


  /*     while  ((endx < 0)||(endx > 49)||(endy < 0)||(endy > 49)||(Grid1[endx][endy] != 0)){
            endx += ThreadLocalRandom.current().nextInt(-5, 5 + 1);
            endy += ThreadLocalRandom.current().nextInt(-5, 5 + 1);
        }*/

        Node initialNode2 = new Node(Snakeheadx, Snakeheady);
        Node finalNode2 = new Node(endx, endy);
        AStar aStar2 = new AStar(gridx, gridy, initialNode2, finalNode2);



        for(int i=0;i<gridx;++i) {
            for (int j = 0; j < gridy; ++j) {

                if (!((Grid1[i][j] == 0)||((i == Snakeheadx)&&(j == Snakeheady)))) {
                    aStar2.setBlock(i, j);
                }

            }

        }

        List<Node> path2 = aStar2.findPath();


        if ((path2.size() >= 2)){
            Node next = path2.get(1);
            int Nextx = next.row;
            int Nexty = next.col;
            makeMove(Nextx, Nexty, Snakeheadx, Snakeheady);
        }


    }

    public static int getPathSize(int Snakeheadx, int Snakeheady, int endx, int endy, int gridx, int gridy, int[][] Grid1) {
        Node initialNode2 = new Node(Snakeheadx, Snakeheady);
        Node finalNode2 = new Node(endx, endy);
        AStar aStar2 = new AStar(gridx, gridy, initialNode2, finalNode2);

        Grid1[endx][endy] = 0;

        for(int i=0;i<gridx;++i) {
            for (int j = 0; j < gridy; ++j) {

                if (!((Grid1[i][j] == 0)||((i == Snakeheadx)&&(j == Snakeheady)))) {
                    aStar2.setBlock(i, j);
                }

            }

        }

        List<Node> path2 = aStar2.findPath();

        return path2.size();

    }
    public static void lastResort(int Snakeheadx, int Snakeheady, int endx, int endy, int gridx, int gridy, ArrayList<String> toDraw, int nSnakes, int mySnakeNum, int Snaketailx, int Snaketaily) {

        //Draw new grid
        int[][] Grid2 = new int[gridx][gridy];
        for(int i = 0; i < gridx ; i++) {
            for(int j = 0 ; j <gridy;j++) {
                Grid2[i][j]= 0;
            }
        }

        for (int j = 0; j < toDraw.size(); j++){
            if (j - 5 == mySnakeNum){
                drawSnake(toDraw.get(j), j - 5, Grid2, 0, false);
                continue;

            }
            if (j < 6) {
                drawSnake(toDraw.get(j), nSnakes + 1, Grid2, 0, false);
            }
            else {
                drawSnake(toDraw.get(j), j - 5, Grid2, 0, false);
            }

        }

        if (getPathSize(Snakeheadx,Snakeheady,endx,endy,gridx,gridy,Grid2) != 0) {
            Travel(Snakeheadx, Snakeheady, endx, endy, gridx, gridy, Grid2);
        }
        else if (getPathSize(Snakeheadx,Snakeheady,Snaketailx,Snaketaily,gridx,gridy,Grid2) != 0) {
            Travel(Snakeheadx, Snakeheady, Snaketailx, Snaketaily, gridx, gridy, Grid2);
        }
        else {
            int x = Snakeheadx;
            int y = Snakeheady;


            if ((x != 0)&&(Grid2[x - 1][y] == 0)){
                makeMove(x-1,y,x,y);
            }
            else if ((x != 49)&&(Grid2[x + 1][y] == 0)) {
                makeMove(x+1,y,x,y);
            }
            else if ((y != 0)&&(Grid2[x][y - 1] == 0)) {
                makeMove(x,y-1,x,y);
            }
            else if ((y != 49)&&(Grid2[x][y + 1] == 0)) {
                makeMove(x,y+1,x,y);
            }

        }

    }
    public static boolean worthIt(int applex, int appley, int mySnakeheadx, int mySnakeheady, int gridx, int gridy, int[][] Grid1, ArrayList<String> heads, ArrayList<String> toDraw, int myLength) {

        //Draw new grid
        int[][] Grid2 = new int[gridx][gridy];
        for(int i = 0; i < gridx ; i++) {
            for(int j = 0 ; j <gridy;j++) {
                Grid2[i][j]= 0;
            }
        }

        for (int j = 0; j < toDraw.size(); j++){
            if (j < 6) {
                drawSnake(toDraw.get(j), j + 1, Grid2, 1, true);
            }
            else {
                drawSnake(toDraw.get(j), j + 1, Grid2, 1, false);
            }

        }



        int headx = 0;
        int heady = 0;
        int minPath = 1000;
        int currPath;

        int myPath = getPathSize(mySnakeheadx,mySnakeheady,applex,appley, gridx, gridy, Grid1);
        int betterThanMe = 0;

        for (int i = 6; i < heads.size(); i++) {
            String cords []= heads.get(i).split(",");

            headx = Integer.parseInt(cords[0]);
            heady = Integer.parseInt(cords[1]);

            currPath = getPathSize(headx,heady,applex,appley, gridx, gridy, Grid2);
            if (currPath < myPath){
                betterThanMe += 1;
            }


            if (currPath < minPath){
                minPath = currPath;
            }

        }


        if ((minPath <= 3)||(myPath == 0)||(betterThanMe >= 2)){

            return false;
        }

        if (myPath < minPath){
            return true;
        }

        double ratio = (minPath * 1.0)/(myPath * 1.0);

        double howRisky = 0.002 * myLength + 0.37;

        if (howRisky > 0.8){
            howRisky = 0.8;
        }

        if (ratio <= howRisky){
            return false;
        }
        else {
            return true;
        }

    }





    public static void drawSnake(String S, int num, int[][] m, int risk, boolean z) {

        String[] p = S.split(" ");


        //increase zombie head

        if ((risk == 2) || (z == true)) {
            String[] v = p[0].split(",");
            int x = Integer.parseInt(v[0]);
            int y = Integer.parseInt(v[1]);

            if (x != 0) {
                m[x - 1][y] = num;
            }
            if (x != 49) {
                m[x + 1][y] = num;
            }
            if (y != 0) {
                m[x][y - 1] = num;
            }
            if (y != 49) {
                m[x][y + 1] = num;
            }

        }


        for(int k = 0; k < p.length; k++) {

            if((k+1) >= p.length) {
                drawLine(m,p[k-1],p[k],num);
            }else {

                drawLine(m,p[k],p[k+1],num);
            }
        }


    }
    //Draw Line Draws the snake Based on the corners and edges. Returns String of p
    public static void drawLine(int[][] m, String p1, String p2, int num){

        String[] v1 = p1.split(",");
        String[] v2 = p2.split(",");

        int y1 = Integer.parseInt(v1[1]);
        int y2 = Integer.parseInt(v2[1]);
        int x1 = Integer.parseInt(v1[0]);
        int x2 = Integer.parseInt(v2[0]);


        if((x1 == x2) && (y2 > y1)) {
            for(int i = y1; i <= y2; i++) {
                m[x1][i] = num;
            }
        }

        if((y1 == y2) && (x2 > x1)) {
            for(int i = x1; i <= x2; i++) {
                m[i][y1] = num;
            }
        }

        if((x1 == x2) && (y1 > y2)) {
            for(int i = y2; i <= y1; i++) {
                m[x1][i] = num;
            }
        }

        if((y1 == y2) && (x1 > x2)) {
            for (int i = x2; i <= x1; i++) {
                m[i][y1] = num;
            }
        }

    }

    public static void makeMove(int  Nextxco, int  NextyCo, int Snakeheadx, int Snakeheady){
        int move = 0;
        if (Nextxco == Snakeheadx){
            if(NextyCo > Snakeheady){
                move = 1;
            }
            else{
                move = 0;
            }

        }
        else if (NextyCo == Snakeheady){
            if(Nextxco > Snakeheadx){
                move = 3;
            }
            else{
                move = 2;
            }
        }


        System.out.println(move);
    }



}

