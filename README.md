# MiniClinic 社區診所掛號系統

一個以 Spring Boot 實作的社區診所掛號系統，支援醫師登入、病患掛號、掛號狀態管理與全院營運數據統計。

## 線上 Demo

請替換成你的 Render 部署網址（例如：https://miniclinic-414570198.onrender.com）

## 技術棧

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Thymeleaf
- SQLite（開發環境）
- PostgreSQL（生產環境）
- BCrypt（密碼雜湊安全防護）

## 功能清單

- 醫師密碼登入與安全登出驗證
- 醫師個人 Dashboard（管理當日門診掛號摘要）
- 線上即時掛號與病歷快速建檔（CRUD）
- 掛號臨床狀態調整（看診完成、取消掛號）
- 全院營運數據指標外部統計 RESTful API

## 本機執行步驟

1. 複製專案到本地：
```bash
git clone [https://github.com/rex769/miniclinic.git]