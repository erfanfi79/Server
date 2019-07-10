package models;

import java.io.*;
import java.util.ArrayList;

public class LoginMenu {
    private static ArrayList<Account> users = new ArrayList<Account>();
    private static ArrayList<Account> onlineUsers = new ArrayList<Account>();
    private static LoginMenu loginMenu;

    static {
        initializeUsers();
    }

    public static LoginMenu getInstance() {
        if (loginMenu == null)
            loginMenu = new LoginMenu();
        return loginMenu;
    }

    public Account createAccount(String userName, String passWord) {
        Account account = new Account();
        account.setPassword(passWord);
        account.setUserName(userName);
        users.add(account);
        return account;
    }

    public void saveUserNames() {
        for (Account account : users)
            try {
                FileWriter writer = new FileWriter("listOfUserName.txt");
                writer.write(account.getUserName());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public boolean checkIfAccountExist(String userName) {
        for (Account account : users)
            if (account.getUserName().equals(userName))
                return true;
        return false;
    }

    public Account login(String userName, String passWord) {
        for (Account account : users) {
            if (account.getUserName().equals(userName)) {
                if (account.isPasswordCorrect(passWord))
                    return account;
            }
        }
        return null;
    }

    private static File[] getFileList(String dirPath) {
        File dir = new File(dirPath);

        File[] fileList = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".ser");
            }
        });
        return fileList;
    }
    private static void initializeUsers() {
        File[] fileList = getFileList("users");

        for (File file : fileList) {
                Account account = null;
                try {
                    FileInputStream is = new FileInputStream("users/" + file.getName());
                    ObjectInputStream ois = new ObjectInputStream(is);
                    account = (Account) ois.readObject();
                    users.add(account);
                    ois.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
    }

    public static ArrayList<Account> getOnlineUsers() {
        return onlineUsers;
    }

    public static ArrayList<Account> getUsers() {
        return users;
    }

    public Account getAccountByUserName(String userName) {
        for (Account account : users) {
            if (account.getUserName().equals(userName))
                return account;
        }
        return null;
    }


}
