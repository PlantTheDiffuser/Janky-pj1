package org.atdp;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

import spark.Filter;

/**
 * Entrypoint for the MegaMaze backend. There should be no need to edit this file.
 */
public class App {
    public static Maze currMaze;
    public static void main(String[] args) {

        Gson gson = new Gson();

        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
        });

        get("/genmaze", "application/json", (req, res) -> {
            res.type("application/json");
            int height = Integer.parseInt(req.queryParams("height"));
            int width = Integer.parseInt(req.queryParams("width"));
            int seed = Integer.parseInt(req.queryParams("seed"));
            currMaze = new Maze(height, width, seed);
            return gson.toJson(new CleanedMaze(currMaze));
        });

        get("/solvemaze", "application/json", (req, res) -> {
            res.type("application/json");
            MazeSolver solver = new MazeSolver();
            return gson.toJson(solver.solve(currMaze));
        });

        get("/testmaze", "application/json", (req, res) -> {
            res.type("application/json");
            int size = Integer.parseInt(req.queryParams("size"));
            return gson.toJson(new CleanedMaze(new TestMaze(size, size)));
        });
    }
}

/**
 * Prepares maze for JSONification by removing circular references.
 */
class CleanedMaze {
    int rows;
    int cols;
    HashMap<Integer, ArrayList<Integer>> connections;

    CleanedMaze(Maze m) {
        rows = m.getHeight();
        cols = m.getWidth();
        connections = new HashMap<>();
        for(Vertex v : m.vertices) {
            connections.put(v.id, new ArrayList<>());
            for (Vertex u : v.edges) {
                connections.get(v.id).add(u.id);
            }
        }
    }
}

/**
 * A very basic, hard-coded 3x3 maze for testing purposes.
 */
class TestMaze extends Maze {

    private int type;

    public TestMaze(int rows, int cols) {
        super(rows, cols);
        this.type = type;
    }

    @Override
    void generateMaze() {
        addEdge(0, 1);
        addEdge(1, 4);
        addEdge(4, 5);
        addEdge(5, 2);
        addEdge(5, 8);
        addEdge(8, 7);
        addEdge(7, 6);
        addEdge(6, 3);
    }
}