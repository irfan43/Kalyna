# Kalyna
Kalyna is Rijndael based algorithm that supports block sizes and key lengths up to 512 bits. Key length can be greater than or equal to block size and number of rounds are 10, 14 and 30 for block sizes of 128, 256 and 512 bits respectively.
### Design
- [Round Functions](https://irfan43.github.io/Kalyna/docs/Design/Round%20Functions.html)
- [Key Expansion](https://irfan43.github.io/Kalyna/docs/Design/Key%20Expansion.html)
- [Encryption and Decryption](https://irfan43.github.io/Kalyna/docs/Design/Encryption%20and%20Decryption.html)

### Cryptanalysis
- [Security Margin](https://irfan43.github.io/Kalyna/docs/Cryptanalysis/Security%20Margin.html)
- [Differential Cryptanalysis](https://irfan43.github.io/Kalyna/docs/Cryptanalysis/Differential%20Cryptanalysis.html)
- [Integral Cryptanalysis](https://irfan43.github.io/Kalyna/docs/Cryptanalysis/Integral%20Cryptanalysis.html)

### Application
- [Chat Server](https://irfan43.github.io/Kalyna/docs/Application/Chat%20Server.html)
- [Chat Client](https://irfan43.github.io/Kalyna/docs/Application/Chat%20Client.html)
- [Kalyna Hash](https://irfan43.github.io/Kalyna/docs/Application/Kalyna%20Hash.html)
- [Kalyna MAC](https://irfan43.github.io/Kalyna/docs/Application/Kalyna%20MAC.html)

---

## Python Notebook
The Python Notebook inside `notebook/Kalyna.ipynb` folder is used to generate the following things
- DDT and LAT analysis
- Mix Columns Look Up table using the Irreducible Polynomial
---
## Application Usage
- To run the code please verify you have **Java version 11** installed in your system
- You can then execute the code using

```bash
java -jar Kalyna.jar
```

- For a help message use

```bash
java -jar Kalyna.jar --help
```

- It has as an interface similar to **git command line** hence we can also get the Help message for each sub command

```bash
java -jar Kalyna.jar <command> --help
```

- Replacing <command> with the command you would like help on - **`(login, server, key, integral, file)`**
---