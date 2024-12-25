package com.fish;

import com.fish.data.Note;
import com.fish.data.NoteHandler;
import com.formdev.flatlaf.FlatLightLaf;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class Funtool {

    private JFrame jFrame = new JFrame("Funtool 0.0.1");
    private JTabbedPane tabbedPane;
    private JPanel leftPanel = new JPanel();
    private JPanel btnPanel = new JPanel();

    // 小记列表
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> list = new JList<>(listModel);

    private JButton addButton = new JButton("新建");
    private JButton renameButton = new JButton("重命名");
    private JButton delButton = new JButton("删除");
    private JTextArea textArea = new JTextArea();

    public static void main(String[] args) throws SQLException, IOException {
        // 全局样式
        FlatLightLaf.setup();

        EventQueue.invokeLater(() -> {
            new Funtool().init();
        });
    }

    @SneakyThrows
    public void init() {
        log.info("start");

        // 初始化文件
        initFile();

        // 初始化数据表
        NoteHandler.initDbTable();

        // 按钮尺寸
        addButton.setPreferredSize(new Dimension(38, 24));
        renameButton.setPreferredSize(new Dimension(48, 24));
        delButton.setPreferredSize(new Dimension(38, 24));
        // 按钮边距
        Insets insets = new Insets(0, 0, 0, 0);
        addButton.setMargin(insets);
        renameButton.setMargin(insets);
        delButton.setMargin(insets);
        // 按钮面板
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBorder(new EmptyBorder(2, 5, 2, 5));
        btnPanel.add(addButton);
        btnPanel.add(renameButton);
        btnPanel.add(delButton);

        // 按钮事件
        this.addButtonListener();

        // 列表样式
        list.setFixedCellHeight(24);
        // 初始化草稿列表数据
        this.initList();
        // 列表事件
        this.addListListener();

        // 左侧面板
        leftPanel.setPreferredSize(new Dimension(180, 100));
        leftPanel.setMinimumSize(new Dimension(150, 100));
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(btnPanel, BorderLayout.NORTH);
        leftPanel.add(list, BorderLayout.CENTER);

        // 文本域样式
        Font font = textArea.getFont();
        Font newFont = font.deriveFont(16f);
        textArea.setFont(newFont);
        // 文本域事件
        this.addTextAreaListener();
        JScrollPane jScrollPane = new JScrollPane(textArea);
        jScrollPane.setMinimumSize(new Dimension(250, 250));

        // 分割面板
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, jScrollPane);
        jSplitPane.setContinuousLayout(true);

        // 选项卡
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("小记", jSplitPane);

        Translate translate = new Translate();
        tabbedPane.addTab("翻译", translate.getPanel());
        tabbedPane.addTab("REST Client", new JPanel());
        tabbedPane.addTab("JSON格式化", new JPanel());
        tabbedPane.addTab("CRON构造器", new JPanel());
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // 窗口
        jFrame.add(tabbedPane, BorderLayout.CENTER);
        jFrame.setMinimumSize(new Dimension(600, 500));
        jFrame.setPreferredSize(new Dimension(800, 600));
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setResizable(true);
        jFrame.setVisible(true);
    }

    // 初始化文件
    public void initFile() throws IOException {
        String usrHome = System.getProperty("user.home");
        File folder = new File(usrHome + File.separator + ".funtool");
        if (folder.exists()) {
            log.info(".funtool目录已存在");
        } else {
            log.info(".funtool目录不存在");
            boolean mkdir = folder.mkdir();
            if (mkdir) {
                log.info(".funtool目录创建成功");
            } else {
                log.info(".funtool目录创建失败");
            }
        }

        File dbFile = new File(usrHome + File.separator + ".funtool" + File.separator + "funtool.db");
        if (dbFile.exists()) {
            log.info("funtool.db已存在");
        } else {
            log.info("funtool.db不存在");
            boolean newFile = dbFile.createNewFile();
            if (newFile) {
                log.info("funtool.db创建成功");
            } else {
                log.info("funtool.db创建失败");
            }
        }
    }

    // 初始化草稿列表数据
    public void initList() {
        List<Note> notes = NoteHandler.getAllNotes();
        if (notes.isEmpty()) {
            String firstName = "新建小记1";
            NoteHandler.addNote(firstName, "");
            listModel.addElement(firstName);
        } else {
            for (Note note : notes) {
                listModel.addElement(note.getNoteName());
            }
            textArea.setText(notes.get(0).getContent());
            list.setSelectedIndex(0);
        }
    }

    // 文本域事件
    public void addTextAreaListener() {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                NoteHandler.updateNoteByName(list.getSelectedValue(), textArea.getText());
//                log.info("insertUpdate");
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                NoteHandler.updateNoteByName(list.getSelectedValue(), textArea.getText());
//                log.info("removeUpdate");
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                NoteHandler.updateNoteByName(list.getSelectedValue(), textArea.getText());
//                log.info("changedUpdate");
            }
        });
    }

    // 列表事件
    public void addListListener() {
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                List<Note> notes = NoteHandler.getAllNotes();
                Optional<Note> first = notes.stream().filter(e -> Objects.equals(e.getNoteName(), list.getSelectedValue())).findFirst();
                if (first.isPresent()) {
                    textArea.setText(first.get().getContent());
                }
            }
        });
    }

    // 按钮事件
    public void addButtonListener() {
        // 新增
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Enumeration<String> elements = listModel.elements();
                List<String> list = new ArrayList<>();
                while (elements.hasMoreElements()) {
                    list.add(elements.nextElement());
                }
                List<Integer> indexList = list.stream().filter(e -> e.startsWith("新建小记"))
                        .map(e -> Integer.parseInt(e.replace("新建小记", "")))
                        .collect(Collectors.toList());
                if (indexList.isEmpty()) {
                    NoteHandler.addNote("新建小记1", "");
                    listModel.add(0, "新建小记1");
                } else {
                    String name = "新建小记" + (Collections.max(indexList) + 1);
                    NoteHandler.addNote(name, "");
                    listModel.add(0, name);
                }
                Funtool.this.list.setSelectedIndex(0);
            }
        });

        // 重命名
        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String input = (String) JOptionPane.showInputDialog(jFrame, "请输入名称", "重命名", JOptionPane.PLAIN_MESSAGE, null, null, list.getSelectedValue());
                if (input != null && input.length() > 0) {
                    List<Note> allNotes = NoteHandler.getAllNotes();
                    Optional<Note> first = allNotes.stream().filter(e -> Objects.equals(e.getNoteName(), input)).findFirst();
                    if (!first.isPresent()) {
                        NoteHandler.updateName(list.getSelectedValue(), input);
                        listModel.set(list.getSelectedIndex(), input);
                    }
                }
            }
        });

        // 删除
        delButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listModel.size() != 1) {
                    NoteHandler.deleteNoteByName(list.getSelectedValue());
                    listModel.removeElement(list.getSelectedValue());
                    list.setSelectedIndex(0);
                }
            }
        });
    }
}
