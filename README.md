# KTTKPM_tuan01
## Chạy RabbitMQ server trên docker:
  ```
  cd rabbitmq-server
  docker-compose up --build -d
  ```
Truy cập [http://localhost:15672](http://localhost:15672/)

## Config chatapp trên IntelliJ
### Mở thư mục chatapp trên IntelliJ
- Nhấn vào dropdown trên thanh công cụ IntelliJ và chọn **Edit Configurations...**
  <img src="/images/configuration/image2.png" alt=""/>

- Nhấn vào dấu **+** sau đó chọn **Application**
  <img src="/images/configuration/image3.png" alt=""/>
  
- Tạo 2 Application là **Producer** và **Consumer**, xem ví dụ bên dưới
  <img src="/images/configuration/image4.png" alt=""/>
  <img src="/images/configuration/image5.png" alt=""/>
  
- Nhấn vào dấu **+** sau đó chọn **Compound**
  <img src="/images/configuration/image6.png" alt=""/>
  
- Đặt tên cho **Compound** và tiếp tục chọn dấu **+**, sau đó thêm 2 Application vừa tạo ở bước trên vào
  <img src="/images/configuration/image7.png" alt=""/>
  
- Đảm bảo dropdown chọn đúng **tên compound**, sau đó nhấn nút **Run** hoặc **Shift + F10**
  <img src="/images/configuration/image8.png" alt=""/>
  
- Hoàn thành, xem Console hoặc RabbitMQ server để thấy kết quả
