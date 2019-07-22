import acm.graphics.*;
import acm.program.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;

public class Pathfinding extends GraphicsProgram {


    private static final int GRID_SIZE = 20;
    private static final int SQUARE_SIZE = 40;
    private int STARTx = 0; //x coordinate of start node
    private int STARTy = 0; //y coordinate of start node
    private int ENDx = GRID_SIZE - 1; //x coordinate of end node
    private int ENDy = GRID_SIZE - 1; //y coordinate of end node
    private int mode = 1; // sets behavior when clicked or dragged on grid
    private int sMode = 1; // sets searching mode
    private boolean pathfinding = true;
    private boolean isPath = false;
    private static final int DELAY = 5; // delay between each turn of pathfinding
    private Map<GObject, GObject> visited; // visited squares hashmap
    private GRect[][] squares = new GRect[GRID_SIZE][GRID_SIZE]; // array of all squares
    private boolean clear = false; // if set true, clears the screen

    //buttons
    private GLabel algo; // writes current pathfinding algorithm
    private GLabel pathLabel; // writes current stage of program


    public static void main(String[] args) {
        new Pathfinding().start(args);
    }

    public void run() {
        setSize(GRID_SIZE * SQUARE_SIZE + 200, GRID_SIZE * SQUARE_SIZE + 200);
        addMouseListeners();
        addKeyListeners();
        setArray();
        setStartStop();
        setLabels();
        while (true) {
            //waiting for uset to start pathfinding
            while (pathfinding) {
                pause(50);
                if (clear) {
                    clear = false;
                    clear();
                }
            }
            //clear paths before start of pathfinding
            clearField();
            startPathFinding();
            //draws path if there is a path
            if (isPath == true) {
                drawPath();
                pathLabel.setLabel("Path found");
            } else {
                pathLabel.setLabel("Path not found");
            }
            isPath = false;
        }

    }

    public void mouseDragged(MouseEvent e) {
        GObject object = getElementAt(e.getX(), e.getY());
        if (object != null && object.getWidth() == object.getHeight()) {
            GRect rect = (GRect) object;
            int x = (int) rect.getX() / SQUARE_SIZE;
            int y = (int) rect.getY() / SQUARE_SIZE;
            Color color = rect.getFillColor();
            if (mode == 1) {
                if (rect.getFillColor().equals(Color.WHITE) || color.equals(Color.PINK) || color.equals(Color.YELLOW)) {
                    rect.setFillColor(Color.BLACK);
                }

            } else if (mode == 0) {
                if (color == Color.BLACK) {
                    rect.setFillColor(Color.WHITE);
                }
            } else if (mode == 2) {
                if (color != Color.RED) {
                    squares[STARTx][STARTy].setFillColor(Color.WHITE);
                    rect.setFillColor(Color.BLUE);
                    STARTx = (int) (rect.getX() / SQUARE_SIZE);
                    STARTy = (int) (rect.getY() / SQUARE_SIZE);
                    squares[STARTx][STARTy] = rect;
                }
            } else if (mode == 3) {
                if (color != Color.BLUE) {
                    squares[ENDx][ENDy].setFillColor(Color.WHITE);
                    rect.setFillColor(Color.RED);
                    ENDx = (int) (rect.getX() / SQUARE_SIZE);
                    ENDy = (int) (rect.getY() / SQUARE_SIZE);
                    squares[ENDx][ENDy] = rect;
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        GObject object = getElementAt(e.getX(), e.getY());
        if (object != null && object.getWidth() == object.getHeight()) {
            GRect rect = (GRect) object;
            int x = (int) rect.getX() / SQUARE_SIZE;
            int y = (int) rect.getY() / SQUARE_SIZE;
            Color color = rect.getFillColor();
            if (mode == 1) {
                if (rect.getFillColor().equals(Color.WHITE) || color.equals(Color.PINK) || color.equals(Color.YELLOW)) {
                    rect.setFillColor(Color.BLACK);
                }

            } else if (mode == 0) {
                if (color == Color.BLACK) {
                    rect.setFillColor(Color.WHITE);
                }
            } else if (mode == 2) {
                if (color != Color.RED) {
                    squares[STARTx][STARTy].setFillColor(Color.WHITE);
                    rect.setFillColor(Color.BLUE);
                    STARTx = (int) (rect.getX() / SQUARE_SIZE);
                    STARTy = (int) (rect.getY() / SQUARE_SIZE);
                    squares[STARTx][STARTy] = rect;
                }
            } else if (mode == 3) {
                if (color != Color.BLUE) {
                    squares[ENDx][ENDy].setFillColor(Color.WHITE);
                    rect.setFillColor(Color.RED);
                    ENDx = (int) (rect.getX() / SQUARE_SIZE);
                    ENDy = (int) (rect.getY() / SQUARE_SIZE);
                    squares[ENDx][ENDy] = rect;
                }
            }
        }

    }

    //buttons to work with program
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            mode = 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            mode = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            pathfinding = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_X) {
            if (sMode != 3) {
                sMode++;
            } else {
                sMode = 1;
            }
            if (sMode == 1) {
                algo.setLabel("BFS");
            } else if (sMode == 2) {
                algo.setLabel("A*");
            } else if (sMode == 3) {
                algo.setLabel("GFS");
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            mode = 2;
        }
        if (e.getKeyCode() == KeyEvent.VK_F) {
            mode = 3;
        }
        if (e.getKeyCode() == KeyEvent.VK_C) {
            clear = true;
        }

    }

    //sets 2d array of GRects (my grid)
    private void setArray() {

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                GRect grid = new GRect(SQUARE_SIZE, SQUARE_SIZE);
                grid.setFilled(true);
                grid.setFillColor(Color.WHITE);
                add(grid, i * SQUARE_SIZE, j * SQUARE_SIZE);
                squares[i][j] = grid;

            }
        }

    }

    //sets start node and end node at start of program
    private void setStartStop() {
        GRect startRect = (GRect) getElementAt(STARTx * SQUARE_SIZE, STARTy * SQUARE_SIZE);
        startRect.setFillColor(Color.BLUE);
        GRect endRect = (GRect) getElementAt(ENDx * SQUARE_SIZE, ENDy * SQUARE_SIZE);
        endRect.setFillColor(Color.RED);
    }

    //decides what algorithm to use based on user configuration
    private void startPathFinding() {
        pathfinding = true;
        pathLabel.setLabel("FINDING PATH...");
        if (sMode == 1) {
            bfs();
        } else if (sMode == 2) {
            a();
        } else if (sMode == 3) {
            gfs();
        }

    }

    //draws path based on hasmap of squares and what square did they come from
    private void drawPath() {
        GObject current = squares[ENDx][ENDy]; //start from end node and move through path to start node
        GObject start = squares[STARTx][STARTy];
        ((GRect) current).setFillColor(Color.RED);
        current = visited.get(current);
        while (current != start) {
            ((GRect) current).setFillColor(Color.YELLOW);
            current = visited.get(current);
        }
    }

    // breadth first algorithm
    private void bfs() {
        Queue<GObject> frontier = new LinkedList<>();// queue of squares to go to
        GObject start = squares[STARTx][STARTy];
        GObject goal = squares[ENDx][ENDy];
        frontier.add(start);//add starting square to queue
        visited = new HashMap<GObject, GObject>();// visited squares
        visited.put(start, null); // add start to visited
        while (frontier.peek() != null) {
            GRect current = (GRect) frontier.remove();
            GRect leftNeighbour = (GRect) getElementAt(current.getX() - SQUARE_SIZE, current.getY());
            GRect rightNeighbour = (GRect) getElementAt(current.getX() + SQUARE_SIZE, current.getY());
            GRect topNeighbour = (GRect) getElementAt(current.getX(), current.getY() + SQUARE_SIZE);
            GRect botNeighbour = (GRect) getElementAt(current.getX(), current.getY() - SQUARE_SIZE);

            if (current == goal) {
                isPath = true;
                break;
            }
            //4 if statements that check if neighbours are visited and adds them to queue if they are not
            if (leftNeighbour != null && leftNeighbour.getFillColor() != Color.BLACK) {
                if (!visited.containsKey(leftNeighbour)) {
                    visited.put(leftNeighbour, current);
                    frontier.add(leftNeighbour);
                    leftNeighbour.setFillColor(Color.PINK);
                }
            }
            if (rightNeighbour != null && rightNeighbour.getFillColor() != Color.BLACK) {
                if (!visited.containsKey(rightNeighbour)) {
                    visited.put(rightNeighbour, current);
                    frontier.add(rightNeighbour);
                    rightNeighbour.setFillColor(Color.PINK);
                }
            }
            if (topNeighbour != null && topNeighbour.getFillColor() != Color.BLACK) {
                if (!visited.containsKey(topNeighbour)) {
                    visited.put(topNeighbour, current);
                    frontier.add(topNeighbour);
                    topNeighbour.setFillColor(Color.PINK);
                }
            }
            if (botNeighbour != null && botNeighbour.getFillColor() != Color.BLACK) {
                if (!visited.containsKey(botNeighbour)) {
                    visited.put(botNeighbour, current);
                    frontier.add(botNeighbour);
                    botNeighbour.setFillColor(Color.PINK);
                }
            }
            pause(DELAY);
        }
    }

    // a* pathfinding algorithm
    private void a() {
        PriorityQueue<Node> frontier = new PriorityQueue<Node>(5, new Node()); //priority queue that first takes nodes with lower cost
        int cost1; //number of step taken to current location
        int cost2; //calculation of how many steps are needed to get to final node
        int finalcost; // total of 2 costs above
        GRect start = (GRect) getElementAt(STARTx * SQUARE_SIZE, STARTy * SQUARE_SIZE);//starting node
        GRect goal = (GRect) getElementAt(ENDx * SQUARE_SIZE, ENDy * SQUARE_SIZE);//ending node
        frontier.add(new Node(start, 0));//add starting square to queue
        visited = new HashMap<GObject, GObject>();//hashmap of visited squares that hold from what square did squares come from
        visited.put(start, null); // add start to visited
        Map<GRect, Integer> costSoFar = new HashMap<>(); //hashmap of nodes and how many steps were needed to get to current node
        Integer i = 0;
        costSoFar.put(start, i); //looping through nodes and their neighbours
        while (!frontier.isEmpty()) {
            Node currentNode = frontier.remove();
            GRect current = currentNode.getGRect();
            GRect leftNeighbour = (GRect) getElementAt(current.getX() - SQUARE_SIZE, current.getY());
            GRect rightNeighbour = (GRect) getElementAt(current.getX() + SQUARE_SIZE, current.getY());
            GRect topNeighbour = (GRect) getElementAt(current.getX(), current.getY() + SQUARE_SIZE);
            GRect botNeighbour = (GRect) getElementAt(current.getX(), current.getY() - SQUARE_SIZE);

            if (current == goal) {
                isPath = true;
                break;
            }
            //4 if statements to check if neighbours are visited and if not add them to queue with their cost
            if (leftNeighbour != null && leftNeighbour.getFillColor() != Color.BLACK) {
                if (!visited.containsKey(leftNeighbour)) {
                    visited.put(leftNeighbour, current);
                    int newCost = costSoFar.get(current) + 1;
                    costSoFar.put(leftNeighbour, newCost);
                    cost1 = newCost;
                    cost2 = heuristic(leftNeighbour, goal);
                    finalcost = cost1 + cost2;
                    frontier.add(new Node(leftNeighbour, finalcost));
                    leftNeighbour.setFillColor(Color.PINK);
                }
            }
            if (rightNeighbour != null && rightNeighbour.getFillColor() != Color.BLACK) {
                if (!visited.containsKey(rightNeighbour)) {
                    visited.put(rightNeighbour, current);
                    int newCost = costSoFar.get(current) + 1;
                    costSoFar.put(rightNeighbour, newCost);
                    cost1 = newCost;
                    cost2 = heuristic(rightNeighbour, goal);
                    finalcost = cost1 + cost2;
                    frontier.add(new Node(rightNeighbour, finalcost));
                    rightNeighbour.setFillColor(Color.PINK);
                }
            }
            if (topNeighbour != null && topNeighbour.getFillColor() != Color.BLACK) {
                if (!visited.containsKey(topNeighbour)) {
                    visited.put(topNeighbour, current);
                    int newCost = costSoFar.get(current) + 1;
                    costSoFar.put(topNeighbour, newCost);
                    cost1 = newCost;
                    cost2 = heuristic(topNeighbour, goal);
                    finalcost = cost1 + cost2;
                    frontier.add(new Node(topNeighbour, finalcost));
                    topNeighbour.setFillColor(Color.PINK);
                }
            }
            if (botNeighbour != null && botNeighbour.getFillColor() != Color.BLACK) {
                if (!visited.containsKey(botNeighbour)) {
                    visited.put(botNeighbour, current);
                    int newCost = costSoFar.get(current) + 1;
                    costSoFar.put(botNeighbour, newCost);
                    cost1 = newCost;
                    cost2 = heuristic(botNeighbour, goal);
                    finalcost = cost1 + cost2;
                    frontier.add(new Node(botNeighbour, finalcost));
                    botNeighbour.setFillColor(Color.PINK);
                }
            }
            pause(DELAY);

        }
    }

    //greedy first search pathfinding algorithm
    private void gfs() {
        PriorityQueue<Node> frontier = new PriorityQueue<Node>(5, new Node()); //pririty queue that takes nodes with lower cost first
        int finalcost; //number of steps required to get to final node from current node
        GRect start = (GRect) getElementAt(STARTx * SQUARE_SIZE, STARTy * SQUARE_SIZE); //starting node
        GRect goal = (GRect) getElementAt(ENDx * SQUARE_SIZE, ENDy * SQUARE_SIZE);   //end node
        frontier.add(new Node(start, 0));//add starting square to queue with cost 0
        visited = new HashMap<GObject, GObject>();// hashmap that holds visited squares and saves what what square did they come from
        visited.put(start, null); // add start to visited set it didnt come from any node
        while (!frontier.isEmpty()) { // looping through neighbors untiln there arent any more neighbours left
            Node currentNode = frontier.remove();
            GRect current = currentNode.getGRect();
            GRect leftNeighbour = (GRect) getElementAt(current.getX() - SQUARE_SIZE, current.getY());
            GRect rightNeighbour = (GRect) getElementAt(current.getX() + SQUARE_SIZE, current.getY());
            GRect topNeighbour = (GRect) getElementAt(current.getX(), current.getY() + SQUARE_SIZE);
            GRect botNeighbour = (GRect) getElementAt(current.getX(), current.getY() - SQUARE_SIZE);

            if (current == goal) {
                isPath = true;
                break;
            }

            // 4 if statements to check all 4 directions for neighbours
            // if there is a neighbour and it isn`t visited yet i put it in queue with cost
            if (leftNeighbour != null && leftNeighbour.getFillColor() != Color.BLACK) {
                if (!visited.containsKey(leftNeighbour)) {
                    visited.put(leftNeighbour, current);
                    finalcost = heuristic(leftNeighbour, goal);
                    frontier.add(new Node(leftNeighbour, finalcost));
                    leftNeighbour.setFillColor(Color.PINK);
                }
            }
            if (rightNeighbour != null && rightNeighbour.getFillColor() != Color.BLACK) {
                if (!visited.containsKey(rightNeighbour)) {
                    visited.put(rightNeighbour, current);
                    finalcost = heuristic(rightNeighbour, goal);
                    frontier.add(new Node(rightNeighbour, finalcost));
                    rightNeighbour.setFillColor(Color.PINK);
                }
            }
            if (topNeighbour != null && topNeighbour.getFillColor() != Color.BLACK) {
                if (!visited.containsKey(topNeighbour)) {
                    visited.put(topNeighbour, current);
                    finalcost = heuristic(topNeighbour, goal);
                    frontier.add(new Node(topNeighbour, finalcost));
                    topNeighbour.setFillColor(Color.PINK);
                }
            }
            if (botNeighbour != null && botNeighbour.getFillColor() != Color.BLACK) {
                if (!visited.containsKey(botNeighbour)) {
                    visited.put(botNeighbour, current);
                    finalcost = heuristic(botNeighbour, goal);
                    frontier.add(new Node(botNeighbour, finalcost));
                    botNeighbour.setFillColor(Color.PINK);
                }
            }
            pause(DELAY);

        }
    }

    // setup of labels for using program and labels for displaying algorithm in use and current status of pathfinding
    private void setLabels() {
        GLabel changeAlgo = new GLabel("press X to change");
        changeAlgo.setFont("Helvetica-Bold-15");
        add(changeAlgo, getWidth() - 100 - changeAlgo.getWidth() / 2, 50);

        algo = new GLabel("BFS");
        algo.setFont("Helvetica-Bold-40");
        add(algo, getWidth() - 100 - algo.getWidth() / 2, 100);

        GLabel start = new GLabel("press enter to START");
        start.setFont("Helvetica-Bold-15");
        add(start, getWidth() - 100 - start.getWidth() / 2, 180);

        GLabel add = new GLabel("press A to ADD");
        add.setFont("Helvetica-Bold-15");
        add(add, getWidth() - 100 - add.getWidth() / 2, 280);

        GLabel remove = new GLabel("press D to REMOVE");
        remove.setFont("Helvetica-Bold-15");
        add(remove, getWidth() - 100 - remove.getWidth() / 2, 310);

        GLabel addStart = new GLabel("press S to move START");
        addStart.setFont("Helvetica-Bold-13");
        add(addStart, getWidth() - 100 - addStart.getWidth() / 2, 340);

        GLabel addEnd = new GLabel("press F to move END");
        addEnd.setFont("Helvetica-Bold-13");
        add(addEnd, getWidth() - 100 - addEnd.getWidth() / 2, 370);

        pathLabel = new GLabel("PATHFINDING");
        pathLabel.setFont("Helvetica-Bold-40");
        add(pathLabel, getWidth() / 2 - pathLabel.getWidth() / 2, getHeight() - 100 + pathLabel.getAscent());

        GLabel clearLabel = new GLabel("press C to CLEAR");
        clearLabel.setFont("Helvetica-Bold-13");
        add(clearLabel, getWidth() - 100 - clearLabel.getWidth() / 2, 400);


    }

    //clears old path and visited squares for rerun of pathfinding
    private void clearField() {
        for (GRect[] e : squares) {
            for (GRect elem : e) {
                if (elem.getFillColor() != Color.BLACK && elem.getFillColor() != Color.RED && elem.getFillColor() != Color.BLUE) {
                    elem.setFillColor(Color.WHITE);
                }
            }
        }
    }

    //calculates number of moves to get to place in grid map
    private int heuristic(GRect x, GRect y) {
        int aX = (int) (x.getX() / SQUARE_SIZE);
        int aY = (int) (x.getY() / SQUARE_SIZE);
        int bX = (int) (y.getX() / SQUARE_SIZE);
        int bY = (int) (y.getY() / SQUARE_SIZE);

        return Math.abs(aX - bX) + Math.abs(aY - bY);
    }

    //clears screen, leaves only start and finish
    private void clear() {
        for (GRect[] e : squares) {
            for (GRect elem : e) {
                if (elem.getFillColor() != Color.RED && elem.getFillColor() != Color.BLUE) {
                    elem.setFillColor(Color.WHITE);

                }
            }
        }
    }
}


class Node implements Comparator<Node> {
    public GRect node;
    public int cost;

    public Node() {
    }

    public Node(GRect node, int cost) {
        this.node = node;
        this.cost = cost;
    }

    public GRect getGRect() {
        return node;
    }

    @Override
    public int compare(Node node1, Node node2) {
        if (node1.cost < node2.cost)
            return -1;
        if (node1.cost > node2.cost)
            return 1;
        return 0;
    }
}
