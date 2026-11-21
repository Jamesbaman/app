import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Activity 3: Method of Successive Substitution
 * Loan Interest Stability Model  ->  x = g(x) = 0.85x + 120
 */
public class SuccessiveSubstitutionApp extends JFrame {

    // ---------- Palette ----------
    private static final Color BG     = new Color(0xF6F4F0);
    private static final Color CARD   = Color.WHITE;
    private static final Color INK    = new Color(0x22262B);
    private static final Color MUTED  = new Color(0x7A7F87);
    private static final Color ACCENT = new Color(0x2F4A3F);
    private static final Color ACCENT_HOVER = new Color(0x3D5E50);
    private static final Color LINE   = new Color(0xE6E2DB);
    private static final Color ERROR  = new Color(0xA33B3B);

    private static final String FONT = pickFont("Segoe UI", "Helvetica Neue", "SF Pro Text", "SansSerif");
    private static final String MONO = pickFont("Consolas", "Menlo", "Monospaced");

    // ---------- Model ----------
    private static final double A = 0.85, B = 120;
    private static double g(double x) { return A * x + B; }

    // ---------- UI ----------
    private final JTextField guessField = new JTextField("0");
    private final JTextField iterField  = new JTextField("10");
    private final JLabel errorLabel     = new JLabel(" ");
    private final JLabel resultValue    = new JLabel("—");
    private final JLabel resultCaption  = new JLabel("Run the iteration to see the approximate value");
    private final DefaultTableModel tableModel =
            new DefaultTableModel(new String[]{"Iteration", "x", "Change  |Δx|"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

    public SuccessiveSubstitutionApp() {
        super("Successive Substitution");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(30, 36, 30, 36));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildResult(), BorderLayout.SOUTH);

        setContentPane(root);
        setSize(720, 760);
        setMinimumSize(new Dimension(600, 620));
        setLocationRelativeTo(null);
    }

    // ---------- Sections ----------
    private JComponent buildHeader() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel eyebrow = label("LOAN INTEREST STABILITY MODEL", 11, Font.BOLD, ACCENT);
        JLabel title = label("Successive Substitution", 30, Font.PLAIN, INK);
        title.setFont(new Font(pickFont("Georgia", "Serif"), Font.PLAIN, 30));
        JLabel eq = label("x  =  g(x)  =  0.85x + 120", 14, Font.PLAIN, MUTED);
        eq.setFont(new Font(MONO, Font.PLAIN, 14));

        eyebrow.setAlignmentX(0); title.setAlignmentX(0); eq.setAlignmentX(0);
        p.add(eyebrow);
        p.add(Box.createVerticalStrut(4));
        p.add(title);
        p.add(Box.createVerticalStrut(4));
        p.add(eq);
        return p;
    }

    private JComponent buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(buildInputCard(), BorderLayout.NORTH);
        center.add(buildTableCard(), BorderLayout.CENTER);
        return center;
    }

    private JComponent buildInputCard() {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(20, 22, 18, 22));

        // Convergence note
        double slope = Math.abs(A);
        boolean ok = slope < 1;
        JLabel note = label((ok ? "✓  " : "✗  ") + "Convergence condition   |g′(x)| = "
                + String.format("%.2f", slope) + (ok ? "  <  1" : "  ≥  1"), 13, Font.PLAIN, ok ? ACCENT : ERROR);

        // Inputs row
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.weightx = 1;

        c.gridx = 0; c.insets = new Insets(0, 0, 0, 12);
        row.add(field("Initial guess", guessField), c);
        c.gridx = 1;
        row.add(field("Number of iterations", iterField), c);

        RoundedButton run = new RoundedButton("Run iteration");
        c.gridx = 2; c.weightx = 0; c.insets = new Insets(18, 0, 0, 0);
        row.add(run, c);

        ActionListener go = e -> run();
        run.addActionListener(go);
        guessField.addActionListener(go);
        iterField.addActionListener(go);

        errorLabel.setFont(new Font(FONT, Font.PLAIN, 12));
        errorLabel.setForeground(ERROR);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(errorLabel, BorderLayout.WEST);

        card.add(note, BorderLayout.NORTH);
        card.add(row, BorderLayout.CENTER);
        card.add(south, BorderLayout.SOUTH);
        return card;
    }

    private JComponent buildTableCard() {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(14, 14, 14, 14));

        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setBackground(CARD);
        table.setForeground(INK);
        table.setSelectionBackground(new Color(0xEAF0EC));
        table.setSelectionForeground(INK);
        table.setFont(new Font(MONO, Font.PLAIN, 13));
        table.setFocusable(false);
        table.setRowSelectionAllowed(false);

        DefaultTableCellRenderer center = new CellRenderer(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new CellRenderer(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(right);
        table.getColumnModel().getColumn(2).setCellRenderer(right);
        table.getColumnModel().getColumn(0).setMaxWidth(120);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(0, 34));
        header.setDefaultRenderer(new HeaderRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildResult() {
        RoundedPanel card = new RoundedPanel(ACCENT);
        card.setLayout(new BorderLayout(0, 2));
        card.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel cap = label("APPROXIMATE VALUE", 11, Font.BOLD, new Color(0xB9CBC1));
        resultValue.setFont(new Font(pickFont("Georgia", "Serif"), Font.PLAIN, 32));
        resultValue.setForeground(Color.WHITE);
        resultCaption.setFont(new Font(FONT, Font.PLAIN, 12));
        resultCaption.setForeground(new Color(0xB9CBC1));

        card.add(cap, BorderLayout.NORTH);
        card.add(resultValue, BorderLayout.CENTER);
        card.add(resultCaption, BorderLayout.SOUTH);
        return card;
    }

    // ---------- Logic ----------
    private void run() {
        double x;
        int n;
        try {
            x = Double.parseDouble(guessField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Initial guess must be a number.");
            return;
        }
        try {
            n = Integer.parseInt(iterField.getText().trim());
            if (n < 1 || n > 100000) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showError("Iterations must be a whole number between 1 and 100,000.");
            return;
        }
        showError(" ");
        tableModel.setRowCount(0);

        for (int i = 1; i <= n; i++) {
            double next = g(x);
            tableModel.addRow(new Object[]{
                    i, String.format("%.4f", next), String.format("%.4f", Math.abs(next - x))});
            x = next;
        }
        resultValue.setText(String.format("%.4f", x));
        resultCaption.setText("after " + n + (n == 1 ? " iteration" : " iterations"));
    }

    private void showError(String msg) { errorLabel.setText(msg); }

    // ---------- Helpers ----------
    private static String pickFont(String... names) {
        String[] avail = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String n : names)
            for (String a : avail) if (a.equalsIgnoreCase(n)) return a;
        return names[names.length - 1];
    }

    private static JLabel label(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FONT, style, size));
        l.setForeground(color);
        return l;
    }

    private JComponent field(String caption, JTextField tf) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        JLabel l = label(caption, 12, Font.PLAIN, MUTED);

        tf.setFont(new Font(MONO, Font.PLAIN, 15));
        tf.setForeground(INK);
        tf.setCaretColor(ACCENT);
        tf.setBackground(CARD);
        RoundBorder idle = new RoundBorder(LINE);
        RoundBorder focus = new RoundBorder(ACCENT);
        tf.setBorder(idle);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { tf.setBorder(focus); }
            @Override public void focusLost(FocusEvent e)   { tf.setBorder(idle); }
        });
        p.add(l, BorderLayout.NORTH);
        p.add(tf, BorderLayout.CENTER);
        return p;
    }

    // ---------- Custom components ----------
    private static class RoundedPanel extends JPanel {
        private final Color fill;
        RoundedPanel() { this(CARD); }
        RoundedPanel(Color fill) { this.fill = fill; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            if (fill == CARD) {
                g2.setColor(LINE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundBorder extends AbstractBorder {
        private final Color color;
        RoundBorder(Color color) { this.color = color; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, 12, 12);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(9, 12, 9, 12); }
    }

    private static class RoundedButton extends JButton {
        private boolean hover;
        RoundedButton(String text) {
            super(text);
            setFont(new Font(FONT, Font.BOLD, 13));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(11, 22, 11, 22));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hover ? ACCENT_HOVER : ACCENT);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class CellRenderer extends DefaultTableCellRenderer {
        CellRenderer(int align) { setHorizontalAlignment(align); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                                 boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            setBorder(new EmptyBorder(0, 16, 0, 16));
            setBackground(row % 2 == 0 ? CARD : new Color(0xFAF9F6));
            setForeground(col == 0 ? MUTED : INK);
            return this;
        }
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                                 boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            setFont(new Font(FONT, Font.BOLD, 11));
            setText(String.valueOf(v).toUpperCase());
            setForeground(MUTED);
            setBackground(CARD);
            setHorizontalAlignment(col == 0 ? SwingConstants.CENTER : SwingConstants.RIGHT);
            setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, LINE), new EmptyBorder(0, 16, 0, 16)));
            return this;
        }
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new SuccessiveSubstitutionApp().setVisible(true));
    }
}