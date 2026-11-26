[English](README.md) | Türkçe

# Fonksiyon Çağrısından Infix'e Derleyici

Java ile yazılmış küçük bir kaynak koddan kaynak koda (source-to-source) derleyici.
`add(5, mul(3, 2))` gibi iç içe fonksiyon çağrılarını, mümkün olduğunca az
parantez kullanarak infix matematik ifadesine (`5+3*2`) çevirir.

## Özellikler

- İşaretli sayılar, kesirler, üstel gösterim, boşluklar ve `/* blok yorumlar */` için **lexer**
- **Recursive descent parser**
- Sağdan birleşimli `^` ve `?:` dahil, operatör **önceliği ve birleşimliliği**
- Çıktıda **minimum parantez**
- Geçersiz girdi için hata mesajları

## Desteklenen fonksiyonlar

| Fonksiyon | Çıktı | Öncelik |
|---|---|---|
| `tern(a, b, c)` | `a?b:c` | 1 (en düşük) |
| `add`, `sub` | `+`, `-` | 2 |
| `mul`, `div`, `mod` | `*`, `/`, `%` | 3 |
| `pow` | `^` | 4 (en yüksek) |

`^` ve `?:` sağdan birleşimlidir; diğer tüm operatörler soldan birleşimlidir.

## Örnekler

| Girdi | Çıktı |
|---|---|
| `add(5, mul(3, 2))` | `5+3*2` |
| `add(5, mul(3, sub(10, pow(6, 4))))` | `5+3*(10-6^4)` |
| `mul(add(1, 2), 3)` | `(1+2)*3` |
| `sub(5, sub(2, 1))` | `5-(2-1)` |
| `pow(2, pow(3, 2))` | `2^3^2` |
| `pow(pow(2, 3), 2)` | `(2^3)^2` |
| `tern(1, 2, tern(3, 4, 5))` | `1?2:3?4:5` |
| `tern(1, tern(2, 3, 4), 5)` | `1?2?3:4:5` |
| `add(1, /* comment */ 2)` | `1+2` |
| `add(5,)` | `Error: Expected function or number` |
| `foo(1, 2)` | `Error: Invalid function name` |

## Nasıl çalışır?

Lexer girdiyi token'lara çevirir. Recursive descent parser bu token'ları okur ve
ayrıştırdığı her parça kendi metnini ve önceliğini taşır. İki parçayı
birleştirirken parser, alt ifade üst ifadeden zayıf bağlanıyorsa ya da operatörün
birleşimlilik kuralına göre yanlış yönde eşitlik varsa parantez ekler.

| Dosya | Görevi |
|---|---|
| `src/Token.java` | Token türleri, `Token` sınıfı ve `Lexer` |
| `src/Parser.java` | Recursive descent parser ve parantez mantığı |
| `src/Main.java` | İfadeleri (dosyadan ya da standart girdiden) okur ve sonuçları yazdırır |
| `src/input.txt` | Örnek girdi |

## Nasıl çalıştırılır?

Java 14 veya üstü gerekir (kod switch ifadeleri kullanıyor).

```bash
javac -d out src/*.java
java -cp out Main src/input.txt
```

Argüman verilmezse program ifadeleri standart girdiden, satır satır okur.

## Arka plan

Brno Teknik Üniversitesi'nde (VUT) *Formal Languages and Compilers* dersi için
yazıldı. Lexer'ı, parser'ı ve parantez mantığını kendim yazdım. Yapay zeka
araçlarını yalnızca küçük önerilerde kullandım: boşluk kaçış karakterleri
(`\t`, `\n`, `\r`), hatalar için `RuntimeException` kullanımı ve birleşimlilik
kurallarındaki eşitlik durumu.

## Bilinen sınırlamalar

- Kapatılmamış bir blok yorum hata vermek yerine sessizce atlanıyor.
- `^` operatörünün sol operandı negatif bir sayıysa çıktı parantezsiz yazılıyor
  (`pow(-2, 2)` → `-2^2`); standart matematikte bu genellikle -(2²) olarak okunur.
