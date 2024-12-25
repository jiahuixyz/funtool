package com.fish;

import com.fish.util.TranslateUtil;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Translate {

    private JPanel panel = new JPanel();
    private JPanel topPanel = new JPanel();
    private JButton transBtn = new JButton("翻译");
    private JButton clearBtn = new JButton("清空");
    private JTextArea leftTextArea = new JTextArea();
    private JTextArea rightTextArea = new JTextArea();

    public Translate() {
        transBtn.addActionListener(new ActionListener() {
            @Override
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                String text = leftTextArea.getText();
                if (text != null && !text.isBlank()) {
                    String translateResult = TranslateUtil.translate(text);
                    rightTextArea.setText(translateResult);
                }
            }
        });

        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftTextArea.setText("");
                rightTextArea.setText("");
            }
        });

        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(transBtn);
        topPanel.add(clearBtn);
        topPanel.setBorder(new EmptyBorder(2, 5, 2, 5));

        leftTextArea.setLineWrap(true);
        rightTextArea.setLineWrap(true);
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomPanel.add(new JScrollPane(leftTextArea));
        bottomPanel.add(new JScrollPane(rightTextArea));
        bottomPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        panel.setLayout(new BorderLayout());
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);
    }

    JPanel getPanel() {
        return panel;
    }
}
