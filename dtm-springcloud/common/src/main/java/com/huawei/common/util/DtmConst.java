package com.huawei.common.util;

public class DtmConst {
    private DtmConst() {
    }

    public static class InitSql {
        public static final String CREATE_TABLE_SQL = "create table if not exists account\n" +
            "(\n" +
            "    id    int not null,\n" +
            "    type  int not null,\n" +
            "    money int null,\n" +
            "    primary key (id, type)\n" +
            ");";

        public static final String TRUNCATE_SQL = "truncate table account;";

        public static final String CREATE_TABLE_TRANSACTION_SQL = "create table if not exists account_transaction\n" +
            "(\n" +
            "    id int NOT NULL AUTO_INCREMENT,\n" +
            "    tx_id varchar(128) not null,\n" +
            "    user_id varchar(128) not null,\n" +
            "    money int,\n" +
            "    type varchar(256) not null,\n" +
            "    primary key (id, type)\n" +
            ");";

        public static final String TRUNCATE_TRANSACTION_SQL = "truncate table account_transaction;";

        public static final String INSERT_SQL = "insert ignore into account VALUES (?, 0, ?);";

    }

    public static class QuerySql {
        public static final String QUERY_USER_BY_ID = "select money from account where id = %s for update;";

        public static final String QUERY_BANK_SUM_MONEY = "select sum(money) from account;";
    }

    public static class TransferSql {
        public static final String TRANSFER_IN_SQL = "update account set money = money + ? where  id = ?;";

        public static final String TRANSFER_OUT_SQL = "update account set money = money - ? where  id = ?;";
        //记录流水
        public static final String TRANSFER_INSERT_TRANSACTION = "insert into account_transaction(tx_id,user_id,money,type) values( ?,?,?,?) ";
    }

    public static class Column {
        public static final String MONEY = "money";
    }
}
