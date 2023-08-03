import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;

import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;


import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String request(String source,String type) throws IOException {
        String url = "http://api.interpreter.caiyunai.com/v1/translator";

        String token = "111";

        String requestBody = "{\"source\": [\""+source+"\"], " +
                "\"trans_type\": \""+type+"\", \"request_id\": \"demo\"}";

        URL apiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("content-type", "application/json");
        connection.setRequestProperty("x-authorization", "token " + token);
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            outputStream.write(input, 0, input.length);
        }

        StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = reader.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }
        }

        String response = responseBuilder.toString();
        try {
            // 使用正则表达式匹配目标字符串
            Pattern pattern = Pattern.compile("\"target\":\\[\"(.+?)\"\\]");
            Matcher matcher = pattern.matcher(response);

            // 获取第一个匹配项（目标值）
            String firstValue = null;
            if (matcher.find()) {
                firstValue = matcher.group(1);
            }

            return unicodeToString(firstValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.disconnect();
        return "";
    }


    public static boolean containsChinese(String str) {
        for (char c : str.toCharArray()) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isChinese(char c) {
        // 判断字符的Unicode范围是否属于中文字符范围
        return (c >= '\u4E00' && c <= '\u9FA5');
    }

    public static void showTooltip(Editor editor, VisualPosition position, String content) {
        JLabel tooltipLabel = new JLabel("彩云小译");
        JPanel panel = new JPanel();
        panel.add(tooltipLabel);

        JBPopup tooltip = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(panel, null)
                .setAdText(content)
                .createPopup();

        tooltip.show(new RelativePoint(editor.getContentComponent(), editor.visualPositionToXY(position)));
    }
    public static void replace(Editor editor, AnActionEvent e, String content, SelectionModel selectionModel ){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), content);
            }
        };
        WriteCommandAction.runWriteCommandAction(e.getData(PlatformDataKeys.PROJECT), runnable);

    }

    // 将Unicode编码的字符串转换为中文字符
    private static String unicodeToString(String input) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (input.charAt(i) == '\\') {
                if (i + 6 <= input.length() && input.charAt(i + 1) == 'u') {
                    String unicode = input.substring(i + 2, i + 6);
                    sb.append((char) Integer.parseInt(unicode, 16));
                    i += 6;
                } else {
                    sb.append(input.charAt(i));
                    i++;
                }
            } else {
                sb.append(input.charAt(i));
                i++;
            }
        }
        return sb.toString();
    }
}
