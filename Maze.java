package org.atdp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Contains the basic structure and utilities for generating mazes up to 100x100 in size.
 * Two mazes with the same seed and dimensions are guaranteed to have the same layout.
 *
 * Mazes are rectangular, with tiles numbering from 0 (top left) to (height*width)-1 (bottom right).
 * Tiles are vertices identified in row-major order. For example, a 3x3 maze would be represented
 * as a 9-vertex graph labeled as such:
 *
 * 0 1 2
 * 3 4 5
 * 6 7 8
 *
 * Maze is a child class of Graph. It will be helpful to review the Graph class before you
 * begin writing code.
 *
 */
public class Maze extends Graph {
    private int height, width;
    /**
     * Use this to generate random numbers given a seed!
     * See https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Random.html
     */
    private Random gen;

    /**
     * Takes in a height (# rows), width (# columns), and a seed, and generates a new maze.
     */
    public Maze(int height, int width, int seed) {
        // Call Graph constructor
        super(height * width);

        // Instantiate variables
        this.height = height;
        this.width = width;
        gen = new Random(seed);

        // Generate maze
        generateMaze();
    }

    /**
     * Creates a maze with a random seed from 0 to 32767.
     */
    public Maze(int height, int width) {
        this(height, width, (int)(Math.random() * 32768));
    }

    // Getter functions
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }

    /**
     * Generates this maze given the height, width, and seed specified
     * in the constructor. You will probably need to use getNeighbors().
     *
     * HINT: Try to figure out a way to modify BFS or DFS to create connections
     * as you perform a graph traversal of some sort!
     */
    void generateMaze() {
        // YOUR CODE HERE
        boolean[] visited = new boolean[this.height * this.width];
        Stack<Vertex> vstack = new Stack<>();
        Vertex u = vertices.get(0);
        while (u != vertices.get(this.height * this.width - 1)) {
            vstack.add(u);
            visited[u.id] = true;
            while ((u = addNextEdge(u, visited)) == null) {
                u = vstack.pop();
            }
            vstack.add(u);
            visited[u.id] = true;
        }

        for(int i = 0; i < this.height*this.width; i++) {
            if(visited[i] == false) {
                u = vertices.get(i);
                List<Vertex> neighbors = getNeighbors(u);
                int next_id = gen.nextInt(neighbors.size());
                Vertex v = neighbors.get(next_id);
                if(visited[v.id] == true) {
                    addEdge(u.id, v.id);
                    visited[u.id] = true;
                    continue;
                }
                else {
                    for (Vertex x : neighbors) {
                        if (visited[x.id] == true) {
                            addEdge(u.id, x.id);
                            visited[u.id] = true;
                            break;
                        }
                    }
                }
            }
        }

    }
    Vertex addNextEdge(Vertex u, boolean visited[])
    {
        boolean found = false;
        List<Vertex> neighbors = getNeighbors(u);
        int next_id = gen.nextInt(neighbors.size());
        System.out.println(next_id + ": ");
        Vertex v = neighbors.get(next_id);
        if(visited[v.id] == false) {
            found = true;
        }
        else {
            // check all the other neighbors and pick the one not visited
            for (Vertex x : neighbors) {
                if (visited[x.id] == false) {
                    v = x;
                    found = true;
                    break;
                }
            }
        }
        if(found == false) {
            return null;
        }
        visited[v.id] = true;
        addEdge(u.id, v.id);
        System.out.print(u.id + "----" +v.id);
        System.out.println();
        return v;
    }
    /**
     * Returns the immediate neighbors (top, bottom, left, and right) of a particular vertex V.
     * IMPORTANT: Make sure to handle edge and corner cases! For example, in the 3x3 maze:
     * 0 1 2
     * 3 4 5
     * 6 7 8
     * the neighbors of 4 should be [1, 3, 5, 7],
     * but the neighbors of 6 are only [3, 7].
     */
    List<Vertex> getNeighbors(Vertex v) {
        List<Vertex> neighbors = new ArrayList<>();

        // YOUR CODE HERE
        int id = v.id;
        if(id < width) {
            if(id == 0) {
                checkRight(id, neighbors);
                neighbors.add(vertices.get(id+width));
            }
            else if(id == width-1) {
                checkLeft(id, neighbors);
                neighbors.add(vertices.get(id+width));
            }
            else {
                neighbors.add(vertices.get(id-1));
                neighbors.add(vertices.get(id+1));
                neighbors.add(vertices.get(id+width));
            }
        }
        else if(id >= width*height - width) {
            if(id == width*height - width) {
                neighbors.add(vertices.get(id+1));
                neighbors.add(vertices.get(id-width));
            }
            else if(id == width*height-1) {
                neighbors.add(vertices.get(id-1));
                neighbors.add(vertices.get(id-width));
            }
            else {
                neighbors.add(vertices.get(id-1));
                neighbors.add(vertices.get(id+1));
                neighbors.add(vertices.get(id-width));
            }
        }
        else if(id % width == 0){
            neighbors.add(vertices.get(id-width));
            neighbors.add(vertices.get(id+width));
            neighbors.add(vertices.get(id+1));
        }
        else if((id+1)%width == 0) {
            neighbors.add(vertices.get(id-width));
            neighbors.add(vertices.get(id+width));
            neighbors.add(vertices.get(id-1));
        }
        else {
            neighbors.add(vertices.get(id-width));
            neighbors.add(vertices.get(id+width));
            neighbors.add(vertices.get(id-1));
            neighbors.add(vertices.get(id+1));
        }

        return neighbors;
    }

    private void checkRight(int id, List<Vertex> neighbors){
        neighbors.add(vertices.get(id + 1));
    }
    private void checkLeft(int id, List<Vertex> neighbors){
        neighbors.add(vertices.get(id - 1));
    }

    /**
     * Use this during testing to compare mazes:
     * m1.equals(m2) where m1, m2 are Maze objects
     * Two mazes are equal if they have the same layout and dimensions, 
     * even though they may not be exactly the same object.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Maze)) {
            return false;
        }
        CleanedMaze c1 = new CleanedMaze(this);
        CleanedMaze c2 = new CleanedMaze((Maze)(other));

        if (c1.rows != c2.rows || c1.cols != c2.cols) {
            return false;
        }

        for (int i = 0; i < height * width; i++) {
            for (Integer vInt : c1.connections.get(i)) {
                if (!c2.connections.get(i).contains(vInt)) {
                    return false;
                }
            }
        }

        return true;
    }
}
