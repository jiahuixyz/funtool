package com.fish.data;

import com.fish.util.ConnectionUtil;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class NoteHandler {

    public static void initDbTable() throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        String createTableSql = "create table if not exists note(\n" +
                "  note_id integer primary key autoincrement, \n" +
                "  note_name varhcar(255), \n" +
                "  content TEXT, \n" +
                "  is_delete integer, \n" +
                "  create_time varchar(64) \n" +
                ")\n";
        queryRunner.update(ConnectionUtil.getConnection(), createTableSql);
    }

    @SneakyThrows
    public static List<Note> getAllNotes() {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "select * from note where is_delete = 0 order by note_id desc";
        // 下划线转驼峰
        ResultSetHandler<List<Note>> resultSetHandler = new BeanListHandler<Note>(Note.class, new BasicRowProcessor(new GenerousBeanProcessor()));
        return queryRunner.query(ConnectionUtil.getConnection(), sql, resultSetHandler);
    }

    @SneakyThrows
    public static void addNote(String name, String content) {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "INSERT INTO note (note_name, content, is_delete, create_time)\n" +
                "VALUES (?, ?, 0, CURRENT_TIMESTAMP);";
        queryRunner.update(ConnectionUtil.getConnection(), sql, name, content);
    }

    @SneakyThrows
    public static void deleteNoteByName(String name) {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "update note set is_delete = 1 where note_name = ?";
        queryRunner.update(ConnectionUtil.getConnection(), sql, name);
    }

    @SneakyThrows
    public static void updateNoteByName(String name, String content) {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "update note set content = ? where note_name = ? and is_delete = 0";
        queryRunner.update(ConnectionUtil.getConnection(), sql, content, name);
    }

    @SneakyThrows
    public static void updateName(String oldName, String newName) {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "update note set note_name = ? where note_name = ? and is_delete = 0";
        queryRunner.update(ConnectionUtil.getConnection(), sql, newName, oldName);
    }
}
