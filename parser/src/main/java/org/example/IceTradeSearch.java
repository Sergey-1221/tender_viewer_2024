package org.example;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import okhttp3.*;

public class IceTradeSearch {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:123.0) Gecko/20100101 Firefox/123.0";
    private static final String url = "https://icetrade.by/search/auctions";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public static void search() throws UnsupportedEncodingException {
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("search_text", "");
        searchParams.put("zakup_type[1]", "1");
        searchParams.put("zakup_type[2]", "1");
        searchParams.put("company_title", "");
        searchParams.put("establishment", "0");
        searchParams.put("created_from", "");
        searchParams.put("created_to", "");
        searchParams.put("request_end_from", "");
        searchParams.put("request_end_to", "");
        searchParams.put("onPage", "100");
        searchParams.put("sort", "date:desc");
        searchParams.put("p", "1");

        try {
            while (true) {
                Connection.Response doc_ = Jsoup.connect(url)
                        .ignoreHttpErrors(true)
                        .userAgent(USER_AGENT)
                        .data(searchParams)
                        .execute();
                Document doc = Jsoup.parse(new ByteArrayInputStream(doc_.bodyAsBytes()), "CP1251", "https://icetrade.by");
                Elements data = doc.select("table#auctions-list tr");

                if (data.size() > 1 && data.get(1).child(0).text().contains("Тендеры не найдены")) {
                    break;
                } else {
                    for (Element elem : data.subList(1, data.size())) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        Elements tenderData = elem.select("td");
                        String name = tenderData.get(0).select("a").text().trim();
                        if (name.isEmpty()) {
                            continue;
                        }
                        String tenderCustomer = tenderData.get(1).text().trim();
                        String price = tenderData.get(4).select("span").text().trim();
                        String currency = tenderData.get(4).text().replace(price, "").trim();
                        String tenderUrl = tenderData.get(0).select("a").attr("href");

                        Connection.Response response_ = Jsoup.connect(tenderUrl)
                                .ignoreHttpErrors(true)
                                .userAgent(USER_AGENT)
                                .execute();
                        Document tenderDoc = Jsoup.parse(new ByteArrayInputStream(response_.bodyAsBytes()), "CP1251", "https://icetrade.by");
                        String description;
                        try {
                            description = getText(tenderDoc, "tr.af-title td.afv");
                        }
                        catch (Exception e) {
                            description = null;
                        }
                        String otherInformation;
                        try {
                            otherInformation = getText(tenderDoc, "tr.af-others td.afv");
                        }
                        catch (Exception e) {
                            otherInformation = null;
                        }

                        String customerData;
                        try {
                            customerData = getText(tenderDoc, "tr.af-customer_data td.afv");
                        }
                        catch (Exception e) {
                            customerData = null;
                        }

                        String customerContacts;
                        try {
                            customerContacts = getText(tenderDoc, "tr.af-customer_contacts td.afv");
                        }
                        catch (Exception e) {
                            customerContacts = null;
                        }

                        Long createdDateTimestamp;
                        try {
                            DateTimeFormatter createdDate_formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                            LocalDate createdDate = LocalDate.parse(getText(tenderDoc, "tr.af-created td.afv"), createdDate_formatter);
                            createdDateTimestamp = createdDate.atStartOfDay().toEpochSecond(java.time.OffsetDateTime.now().getOffset());
                        }
                        catch (Exception e) {
                            createdDateTimestamp = null;
                        }

                        Long requestEndDateTimestamp;
                        try {
                            DateTimeFormatter requestEndDate_formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                            LocalDate requestEndDate = LocalDate.parse(getText(tenderDoc, "tr.af-request_end td.afv"), requestEndDate_formatter);
                            requestEndDateTimestamp = requestEndDate.atStartOfDay().toEpochSecond(java.time.OffsetDateTime.now().getOffset());
                        }
                        catch (Exception e) {
                            requestEndDateTimestamp = null;
                        }

                        List<Map<String, String>> files = new ArrayList<>();
                        Elements filesData = tenderDoc.select("a.modal");
                        for (Element fileElem : filesData) {
                            try {
                                files.add(Map.of("name", fileElem.text().trim(), "url", fileElem.attr("href").replace("getFile", "download")));
                            }
                            catch (Exception ignored) { }
                        }

                        List<Map<String, String>> lots = new ArrayList<>();
                        Elements lotsData = tenderDoc.select("form#lotsFrom tr");
                        for (Element lotElem : lotsData) {
                            try {
                                String id = lotElem.attr("id");
                                if (id.equals("lotRow" + (lots.size() + 1))) {
                                    String lot_name = lotElem.select("td.wordBreak").text().trim();
                                    String details = lotElem.select("td").get(2).text().trim().replace("\n", " ");
                                    lots.add(Map.of("name", lot_name, "info", details));
                                }
                            }
                            catch (Exception ignored) { }
                        }

                        Map<String, Object> tenderInfo = new HashMap<>();
                        tenderInfo.put("name", name);
                        tenderInfo.put("customer", tenderCustomer);
                        tenderInfo.put("price", price);
                        tenderInfo.put("currency", currency);
                        tenderInfo.put("url", tenderUrl);
                        tenderInfo.put("description", description);
                        tenderInfo.put("otherInfo", otherInformation);
                        tenderInfo.put("customerData", customerData);
                        tenderInfo.put("customerContacts", customerContacts);
                        tenderInfo.put("createdAt", createdDateTimestamp);
                        tenderInfo.put("closedAt", requestEndDateTimestamp);
                        tenderInfo.put("files", files);
                        tenderInfo.put("lots", lots);
                        tenderInfo.put("vKey", tenderUrl.substring(tenderUrl.lastIndexOf('/') + 1));

                        String apiUrl = System.getenv("API_URL");
                        OkHttpClient client = new OkHttpClient();
                        String gson_out = gson.toJson(tenderInfo);
                        RequestBody body = RequestBody.create(gson_out, MediaType.get("application/json; charset=utf-8"));
                        Request request = new Request.Builder()
                                .url(apiUrl + "/tender")
                                .post(body)
                                .build();
                        client.newCall(request);
                    }

                    int currentPage = Integer.parseInt(searchParams.get("p"));
                    searchParams.put("p", String.valueOf(currentPage + 1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getText(Document doc, String selector) {
        Element element = doc.selectFirst(selector);
        return element != null ? element.text().trim() : null;
    }
}
