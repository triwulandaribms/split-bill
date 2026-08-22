# Cara Menjalankan Program

Cek terlebih dahulu bahwa Java 21 + maven sudah terinstall.

## 1. Clone Repository

Clone Repository terlebih dahulu
```bash
git clone <repository-url>
```
lalu masuk ke folder project
```bash
cd split-bill
```
## 2. Atur Konfigurasi Environment Pada File '.env'

```bash
SPRING_APPLICATION_NAME=

SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=

SPRING_JPA_DATABASE_PLATFORM=
SPRING_JPA_HIBERNATE_DDL_AUTO=
SPRING_JPA_SHOW_SQL=

SPRING_FLYWAY_ENABLED=
SPRING_FLYWAY_LOCATIONS=
SPRING_FLYWAY_BASELINE_ON_MIGRATE=

GITHUB_USERNAME=
```
```bash
GITHUB_USERNAME
```
digunakan untuk menghitung service_charge_pct

## 3. Jalankan aplikasi

Untuk Lokal 
```bash
mvn clean spring-boot:run
```

Untuk Docker
```bash
docker compose up --build
```
jika berhasil running lalu hit endpoint

## 4. Membuat Bill Group

Untuk menjalankan projek split bill ini alur pertama yang dilakukan yaitu membuat bill group terlebih dahulu
Gunakan endpoint ini :

Untuk Lokal
Method POST 
```bash
POST http://localhost:8080/api/groups/add
```
Contoh request :
```bash
{
  "name": "Trip Jawa Timur",
  "participants": [
    "Indra",
    "Asep",
    "Riyan"
  ]
}
```
Contoh Respon :
```bash
{
    "code": "GRP00",
    "message": "Group created successfully",
    "data": {
        "id": 5,
        "name": "Trip Jawa Timur",
        "createdAt": "2026-08-21T19:04:36.1375781",
        "updatedAt": "2026-08-21T19:04:36.1375781"
    }
}
```

Untuk Docker
```bash
curl -X POST http://localhost:4110/api/groups/add ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Trip Bandung\",\"participants\":[\"Andi\",\"Budi\",\"Citra\"]}"
```

## 5. Meilhat Detail Bill Group

Selain membuat bill group bisa juga melihat detailnya dengan hit endpoint :

Untuk Lokal

Method GET
```bash
http://127.0.0.1:8080/api/groups/{id}
```
Contoh Respon :
```bash
{
    "code": "GRP00",
    "message": "Group created successfully",
    "data": {
        "id": 5,
        "name": "Trip Jawa Timur",
        "createdAt": "2026-08-21T19:04:36.1375781",
        "updatedAt": "2026-08-21T19:04:36.1375781"
    }
}
```

## 6. Menambah Expense

Setelah membuat bill group lalu buat Expense
Gunakan endpoint ini :

Untuk Lokal

Method POST
```bash
http://127.0.0.1:8080/api/expenses/add
```
Contoh Request :
```bash
{
  "groupId": 1,
  "paidBy": 3,
  "amount": 300000,
  "description": "Makan pagi",
  "participants": [
    {
      "participantId": 1,
      "shareAmount": 100000
    },
    {
      "participantId": 2,
      "shareAmount": 100000
    },
    {
      "participantId": 3,
      "shareAmount": 100000
    }
  ]
}
```
Contoh Respon :
```bash
{
    "code": "EXP00",
    "message": "Expense created successfully",
    "data": {
        "id": 1,
        "groupId": 1,
        "paidBy": 3,
        "amount": 300000,
        "description": "Makan pagi",
        "createdAt": "2026-08-21T21:56:05.8017956"
    }
}
```
Untuk Docker
```bash
curl -X POST http://localhost:4110/api/expenses/add ^
  -H "Content-Type: application/json" ^
  -d "{\"groupId\":1,\"paidBy\":1,\"amount\":300000,\"description\":\"Makan pagi\",\"participants\":[{\"participantId\":1,\"shareAmount\":100000},{\"participantId\":2,\"shareAmount\":100000},{\"participantId\":3,\"shareAmount\":100000}]}"
```

## 7. Menjalankan Settlement

Jika sudah membuat bill group dan Expensenya selanjutnya jalankan settlementnya
Gunakan endpoint ini :

Untuk Lokal

Method GET
```bash
http://localhost:8080/api/groups/{id}/settlement
```
Contoh Respon :
```bash
{
    "code": "STL00",
    "message": "Settlement calculated successfully",
    "data": {
        "settlements": [
            {
                "from": "Indra",
                "to": "Riyan",
                "amount": 100000
            },
            {
                "from": "Asep",
                "to": "Riyan",
                "amount": 100000
            }
        ],
        "service_charge_pct": 4,
        "service_charge_amount": 12000
    }
}
```
- Penjelasan dari respon settlement berdasarkan id ini bahwa Riyan sebelumnya membayar seluruh expense sebesar
Rp 300.000, sedangkan bagian Riyan sendiri adalah Rp 100.000. Sehingga Indra dan Asep masing-masing perlu 
membayar Rp 100.000 kepada Riyan.

- Bagian settlement calculation di hitung berdasarkan :
```bash
balance = total paid - total share
```
Participant dengan balance positif merupakan creditor, sedangkan participant dengan balance negatif merupakan debtor.

- Service Charge pct dihitung berdasarkan Github Username yaitu dengan rumus :
  ```bash
  GitHub username/sudah dikonversi ke kode ASCII % 10
  ```
- Service Charge Amount dihitung dengan :
  ```bash
  service_charge_amount = total_expense × service_charge_pct / 100
  ```

Untuk Docker
```bash
curl http://localhost:4110/api/groups/{id}/settlement
```

## 8. Cara Menjalankan Unit Test

Setelah program berhasil dijalankan, bisa jalankan juga unit testnya 
```bash
mvn test
```

## 9. Cara Menjalankan Docker

```bash
BELUM DIIMPLEMENTASIKAN
```

## 10. Submission Question

```bash 
What was the hardest design decision you made while building this, and what trade-off did you accept?
```
Keputusan desain tersulit yang saya buat adalah memisahkan logika perhitungan settlement dari SettlementService ke dalam class khusus bernama SettlementCalculator. Perhitungan settlement merupakan bagian penting dari aplikasi karena harus menentukan siapa yang harus membayar dan kepada siapa, sekaligus memastikan semua perhitungan uang menggunakan BigDecimal agar tetap akurat. Dengan pemisahan ini, SettlementService dapat fokus pada pengambilan data dari database dan pembuatan response, sedangkan SettlementCalculator fokus pada perhitungan settlement dan dapat diuji secara terpisah. Trade-off yang saya terima adalah struktur kode menjadi sedikit lebih kompleks karena membutuhkan class tambahan dan beberapa Map sebagai data input, tetapi menurut saya hal ini lebih mudah dipelihara dan diuji dibandingkan menempatkan seluruh logika dalam satu service.


