import mysql.connector


mydb = mysql.connector.connect(
        host="localhost",
        user="root",
        password="Glister123",
        database="GlisterDB"
)

mycursor = mydb.cursor()

mycursor.execute("SHOW TABLES")

for x in mycursor:
    print(x)
