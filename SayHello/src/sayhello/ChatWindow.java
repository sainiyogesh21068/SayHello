/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sayhello;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;


public class ChatWindow {

    private JFrame mMainFrame;

    public ChatWindow(List<ChatEntry> chatContentList) {
        // Create JFrame, set window size and center on screen.
        mMainFrame = new JFrame();
        mMainFrame.setTitle("Chat Window");
        mMainFrame.setSize(360, 600);
        mMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mMainFrame.setLocationRelativeTo(null);

        // Create parent container JPanel for all other JComponents.
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buildChatUI(chatContentList), gbc);

        // Add empty JPanel as an object to fill the empty space available.
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        mainPanel.add(new JPanel(), gbc);

        mMainFrame.getContentPane().add(new JScrollPane(new VerticalScrollPane(mainPanel)));
        mMainFrame.setVisible(true);
    }

    public JPanel buildChatUI(List<ChatEntry> chatContentList) {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new GridBagLayout());
        chatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();

        for (ChatEntry chatEntry : chatContentList) {
            JLabel nameLabel = new JLabel(chatEntry.name);

            BubblePane bubble = new BubblePane(chatPanel, chatEntry.content);

            // Arrange each chat entry based on the user.
            if (chatEntry.type == 1) {
                bubble.setBackground(Color.YELLOW);
                gbc.anchor = GridBagConstraints.WEST;
            }
            else {
                bubble.setBackground(Color.CYAN);
                gbc.anchor = GridBagConstraints.EAST;
            }

            gbc.insets.set(0, 0, 0, 0);
            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.NONE;
            chatPanel.add(nameLabel, gbc);

            if (gbc.anchor == GridBagConstraints.WEST) {
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets.set(0, 0, 10, 40);
                chatPanel.add(bubble, gbc);
            }
            else {
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets.set(0, 40, 10, 0);
                chatPanel.add(bubble,gbc);
            }
        }

        return chatPanel;
    }

    private class BubblePane extends JTextArea {

        private static final long serialVersionUID = -6113801969569504295L;

        private int radius = 10;
        private int strokeThickness = 3;
        private int padding = strokeThickness / 2;
        private JPanel mParent;

        public BubblePane(JPanel parent, String text) {
            mParent = parent;

            setOpaque(false);
            setLineWrap(true);
            setWrapStyleWord(true);
            setEditable(false);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setText(text);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(getBackground());
            int x = padding + strokeThickness;
            int width = getWidth() - (strokeThickness * 2);
            int bottomLineY = getHeight() - strokeThickness;
            g2d.fillRect(x, padding, width, bottomLineY);
            g2d.setRenderingHints(new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON));
            g2d.setStroke(new BasicStroke(strokeThickness));
            RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x, padding,
                    width, bottomLineY, radius, radius);
            Area area = new Area(rect);
            g2d.draw(area);


            int lc = countLines(this);
            GridBagLayout gbl = (GridBagLayout) mParent.getLayout();
            GridBagConstraints constraints = gbl.getConstraints(this);
            if (lc == 1) {
                if (constraints.fill == GridBagConstraints.HORIZONTAL) {
                    constraints.fill = GridBagConstraints.NONE;
                    gbl.setConstraints(this, constraints);
                    this.setSize(
                            getFontMetrics(getFont()).stringWidth(getText()) + 
                            this.getBorder().getBorderInsets(this).left +
                            this.getBorder().getBorderInsets(this).right, 
                            getHeight() +
                            this.getBorder().getBorderInsets(this).top + 
                            this.getBorder().getBorderInsets(this).bottom);
                }
            } else {
                if (constraints.fill == GridBagConstraints.NONE) {
                    constraints.fill = GridBagConstraints.HORIZONTAL;
                    gbl.setConstraints(this, constraints);

                }
            }

            super.paintComponent(g);
        }

        private int countLines(JTextArea textArea) {
            AttributedString text = new AttributedString(textArea.getText());
            FontRenderContext frc = textArea.getFontMetrics(textArea.getFont())
                    .getFontRenderContext();
            AttributedCharacterIterator charIt = text.getIterator();
            LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(charIt, frc);
            float formatWidth = (float) textArea.getSize().width;
            lineMeasurer.setPosition(charIt.getBeginIndex());

            int noLines = 0;
            while (lineMeasurer.getPosition() < charIt.getEndIndex()) {
                lineMeasurer.nextLayout(formatWidth);
                noLines++;
            }

            return noLines;
        }
    }


    /**
     * This class is used to make the JTextArea lines wrap every time the window
     * is resized. Without this, the JTextArea lines will not shrink back if the
     * parent window shrinks. This is achieved by returning true on getScrollableTracksViewportWidth();
     */
    private class VerticalScrollPane extends JPanel implements Scrollable {

        private static final long serialVersionUID = 7477168367035025136L;

        public VerticalScrollPane() {
            this(new GridLayout(0, 1));
        }

        public VerticalScrollPane(LayoutManager lm) {
            super(lm);
        }

        public VerticalScrollPane(Component comp) {
            this();
            add(comp);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect,
                int orientation, int direction) {
            return 10;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect,
                int orientation, int direction) {
            return 100;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /**
     * Class structure for storing a single chat entry in a full conversation.
     */
    public static class ChatEntry {
        public String name;
        public String content;

        // For type 0=sent, 1=received.
        public int type;

        public ChatEntry(String name, String content, int type) {
            this.name = name;
            this.content = content;
            this.type = type;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Simulate a conversation between two users and pass it on to the
                // chat window to test the UI.
                List<ChatEntry> chatContentList = new LinkedList<>();
                chatContentList.add(new ChatEntry("David", "Hey Lori, how are you?", 0));
                chatContentList.add(new ChatEntry("Lori", "Hi David, I'm good. What have you been up to?", 1));
                chatContentList.add(new ChatEntry("David", "I've been super busy with work.", 0));
                chatContentList.add(new ChatEntry("David", "Haven't had much free time to even go out to eat.", 0));
                chatContentList.add(new ChatEntry("Lori", "I know what you mean, I've had to work on projects after projects.", 1));
                chatContentList.add(new ChatEntry("David", "Let's make some time and go to lunch tomorrow!", 0));
                chatContentList.add(new ChatEntry("Lori", "That sounds great, let's do 12pm. I know a great food truck by my building.", 1));
                chatContentList.add(new ChatEntry("David", "Perfect, I'll meet you at the entrance of your building.", 0));
                chatContentList.add(new ChatEntry("Lori", "Awesome, see you tomorrow :)", 1));
                new ChatWindow(chatContentList);
            }
        });
    }

}
/*
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        jLabel1.setText("jLabel1");

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(178, 178, 178)
                .addComponent(jLabel1)
                .addContainerGap(181, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(111, 111, 111))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(97, 97, 97)
                .addComponent(jLabel1)
                .addGap(32, 32, 32)
                .addComponent(jButton1)
                .addContainerGap(130, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
*/