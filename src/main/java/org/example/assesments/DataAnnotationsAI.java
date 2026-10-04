package org.example.assesments;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataAnnotationsAI {


        public static void printMessage(String url){
            try{
                String html = HttpClient.newHttpClient().send(
                        HttpRequest.newBuilder().uri(URI.create(url)).build(),
                        HttpResponse.BodyHandlers.ofString()
                ).body();

                Matcher matcher = Pattern.compile("<td[^>]*>(.*?)</td>", Pattern.DOTALL).matcher(html);
                List<String> cells = new ArrayList<>();
                while (matcher.find()) {
                    // Strip out inner HTML tags (like <p class="..."> and <span>) and trim whitespace
                    String cellContent = matcher.group(1).replaceAll("<[^>]*>", "").trim();
                    cells.add(cellContent);
                }

                int maxX =0, maxY=0;
                List<int[]> points = new ArrayList<>();

                for(int i=3; i+2 < cells.size(); i+=3) {
                    int x = Integer.parseInt(cells.get(i));
                    char c = cells.get(i+1).isEmpty()? ' ' : cells.get(i + 1).charAt(0);
                    int y = Integer.parseInt(cells.get(i+2));

                    points.add(new int[]{x, y, c});
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }

                char[][] grid = new char[maxY + 1][maxX+1];
                for(char[] row: grid) Arrays.fill(row, ' ');
                for(int[] p: points) grid[p[1]][p[0]] = (char) p[2];

                for(char[] row: grid){
                    System.out.println(new String(row));
                }
            }catch(Exception e){
                System.out.println("Error: " + e.getMessage());
            }
        }

    public static void main(String[] args) {
        String uri = "https://docs.google.com/document/d/e/2PACX-1vSvM5gDlNvt7npYHhp_XfsJvuntUhq184By5xO_pA4b_gCWeXb6dM6ZxwN8rE6S4ghUsCj2VKR21oEP/pub";
        printMessage(uri);
    }
}
