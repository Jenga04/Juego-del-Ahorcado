import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

class Game2048 extends JFrame {
    private final String[] bgColors = {
            "#eee4da",
            "#ede0c8",
            "#edc850",
            "#edc53f",
            "#f67c5f",
            "#f65e3b",
            "#edcf72",
            "#edcc61",
            "#f2b179",
            "#f59563",
            "#edc22e"
    };
    private final String[] cellColors = {
            "#776e65",
            "#f9f6f2",
            "#f9f6f2",
            "#f9f6f2",
            "#f9f6f2",
            "#f9f6f2",
            "#f9f6f2",
            "#f9f6f2",
            "#776e65",
            "#f9f6f2",
            "#f9f6f2"
    };
    private final int gridSize = 4;
    private JFrame frame;
    private JPanel gamePanel;
    private JLabel[][] gridCells;
    private int[][] gridValues;
    private boolean compressed;
    private boolean merged;
    private boolean moved;
    private int score;

    public Game2048() {
        frame = new JFrame("2048 Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        gamePanel = new JPanel(new GridLayout(gridSize, gridSize));
        gamePanel.setBackground(new Color(173, 216, 230));
        frame.add(gamePanel, BorderLayout.CENTER);

        gridCells = new JLabel[gridSize][gridSize];
        gridValues = new int[gridSize][gridSize];
        compressed = false;
        merged = false;
        moved = false;
        score = 0;

        initializeBoard();

        frame.setVisible(true);
    }

    private void initializeBoard() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                JLabel label = new JLabel("", SwingConstants.CENTER);
                label.setBackground(new Color(131, 139, 139));
                label.setOpaque(true);
                label.setFont(new Font("Arial", Font.BOLD, 22));
                gamePanel.add(label);

                gridCells[i][j] = label;
                gridValues[i][j] = 0;
            }
        }

        randomCell();
        randomCell();
        paintGrid();
    }

    private void reverse() {
        for (int i = 0; i < gridSize; i++) {
            int left = 0;
            int right = gridSize - 1;
            while (left < right) {
                int temp = gridValues[i][left];
                gridValues[i][left] = gridValues[i][right];
                gridValues[i][right] = temp;
                left++;
                right--;
            }
        }
    }

    private void transpose() {
        int[][] temp = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                temp[i][j] = gridValues[j][i];
            }
        }
        gridValues = temp;
    }

    private void compressGrid() {
        compressed = false;
        int[][] temp = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            int count = 0;
            for (int j = 0; j < gridSize; j++) {
                if (gridValues[i][j] != 0) {
                    temp[i][count] = gridValues[i][j];
                    if (count != j) {
                        compressed = true;
                    }
                    count++;
                }
            }
        }
        gridValues = temp;
    }

    private void mergeGrid() {
        merged = false;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize - 1; j++) {
                if (gridValues[i][j] == gridValues[i][j + 1] && gridValues[i][j] != 0) {
                    gridValues[i][j] *= 2;
                    gridValues[i][j + 1] = 0;
                    score += gridValues[i][j];
                    merged = true;
                }
            }
        }
    }

    private void randomCell() {
        int[] emptyCells = new int[gridSize * gridSize];
        int count = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (gridValues[i][j] == 0) {
                    emptyCells[count] = i * gridSize + j;
                    count++;
                }
            }
        }
        if (count > 0) {
            Random random = new Random();
            int index = emptyCells[random.nextInt(count)];
            int row = index / gridSize;
            int col = index % gridSize;
            gridValues[row][col] = 2;  // New cell with value 2
        }
    }

    private boolean canMerge() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize - 1; j++) {
                if (gridValues[i][j] == gridValues[i][j + 1]) {
                    return true;
                }
            }
        }

        for (int i = 0; i < gridSize - 1; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (gridValues[i + 1][j] == gridValues[i][j]) {
                    return true;
                }
            }
        }

        return false;
    }

    private void paintGrid() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (gridValues[i][j] == 0) {
                    gridCells[i][j].setText("");
                    gridCells[i][j].setBackground(new Color(131, 139, 139));
                } else {
                    gridCells[i][j].setText(String.valueOf(gridValues[i][j]));
                    int value = log2(gridValues[i][j]);
                    gridCells[i][j].setBackground(Color.decode(bgColors[value]));
                    gridCells[i][j].setForeground(Color.decode(cellColors[value]));
                }
            }
        }
    }

    private int log2(int n) {
        return (int) (Math.log(n) / Math.log(2));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Game2048().startGame();
        });
    }

    private void startGame() {
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    transpose();
                    compressGrid();
                    mergeGrid();
                    moved = compressed || merged;
                    compressGrid();
                    transpose();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    transpose();
                    reverse();
                    compressGrid();
                    mergeGrid();
                    moved = compressed || merged;
                    compressGrid();
                    reverse();
                    transpose();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    compressGrid();
                    mergeGrid();
                    moved = compressed || merged;
                    compressGrid();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    reverse();
                    compressGrid();
                    mergeGrid();
                    moved = compressed || merged;
                    compressGrid();
                    reverse();
                }

                paintGrid();
                System.out.println("Score: " + score);

                int flag = 0;
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (gridValues[i][j] == 2048) {
                            flag = 1;
                            break;
                        }
                    }
                }

                if (flag == 1) {
                    boolean won = true;
                    JOptionPane.showMessageDialog(frame, "You Won!!");
                    System.out.println("Won");
                    return;
                }

                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (gridValues[i][j] == 0) {
                            flag = 1;
                            break;
                        }
                    }
                }

                if (!(flag == 1 || canMerge())) {
                    boolean end = true;
                    JOptionPane.showMessageDialog(frame, "Game Over!!!");
                    System.out.println("Over");
                }

                if (moved) {
                    randomCell();
                }

                paintGrid();
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }
}