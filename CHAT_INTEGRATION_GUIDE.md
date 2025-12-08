# 💬 Chat System Integration Guide

Bu dokümantasyon, React uygulamanızda WebSocket tabanlı chat sistemini nasıl entegre edeceğinizi açıklar.

## 📋 İçindekiler

1. [Gerekli Paketler](#gerekli-paketler)
2. [WebSocket Bağlantısı](#websocket-bağlantısı)
3. [Mesaj Gönderme](#mesaj-gönderme)
4. [Mesaj Alma](#mesaj-alma)
5. [Chat Geçmişi](#chat-geçmişi)
6. [Örnek React Hook](#örnek-react-hook)

---

## 🔧 Gerekli Paketler

```bash
npm install sockjs-client stompjs
```

veya

```bash
yarn add sockjs-client stompjs
```

---

## 🔌 WebSocket Bağlantısı

### Endpoint
```
ws://localhost:8081/ws
```

### Bağlantı Kurma

```javascript
import SockJS from 'sockjs-client';
import { Stomp } from 'stompjs';

const connectToChat = (jwtToken) => {
  const socket = new SockJS('http://localhost:8081/ws');
  const stompClient = Stomp.over(socket);

  stompClient.connect(
    { 'Authorization': 'Bearer ' + jwtToken },
    (frame) => {
      console.log('✅ WebSocket bağlantısı başarılı');
      
      // Mesajları dinlemeye başla
      stompClient.subscribe('/user/queue/messages', (message) => {
        const chatMessage = JSON.parse(message.body);
        console.log('📩 Yeni mesaj:', chatMessage);
      });
    },
    (error) => {
      console.error('❌ WebSocket bağlantı hatası:', error);
    }
  );

  return stompClient;
};
```

### JWT Token'dan User ID Çıkarma

```javascript
const getUserIdFromToken = (jwtToken) => {
  try {
    const payload = JSON.parse(atob(jwtToken.split('.')[1]));
    return payload.sub; // User ID
  } catch (error) {
    console.error('JWT parse hatası:', error);
    return null;
  }
};
```

---

## 📤 Mesaj Gönderme

### Endpoint
```
/app/chat (WebSocket üzerinden)
```

### Mesaj Formatı

```typescript
interface MessageDto {
  receiverId: number;
  message: string;
}
```

### Örnek Kullanım

```javascript
const sendMessage = (stompClient, receiverId, messageText) => {
  const messageDto = {
    receiverId: receiverId,
    message: messageText
  };

  stompClient.send('/app/chat', {}, JSON.stringify(messageDto));
};
```

---

## 📥 Mesaj Alma

WebSocket üzerinden gelen mesajlar şu formatta gelir:

```typescript
interface IncomingMessage {
  id: number;
  sender: {
    id: number;
    fullName: string;
    email: string;
    role: string;
  };
  receiver: {
    id: number;
    fullName: string;
    email: string;
    role: string;
  };
  message: string;
  createdAt: string; // ISO 8601 format
}
```

### Subscribe Etme

```javascript
stompClient.subscribe('/user/queue/messages', (message) => {
  const chatMessage = JSON.parse(message.body);
  
  // Mesajı state'e ekle veya UI'da göster
  handleNewMessage(chatMessage);
});
```

---

## 📜 Chat Geçmişi

### REST API Endpoint
```
GET /api/messages/{otherUserId}
```

### Headers
```
Authorization: Bearer {jwtToken}
```

### Response Formatı

```typescript
interface ChatMessageResponseDto {
  id: number;
  senderId: number;
  senderName: string;
  receiverId: number;
  receiverName: string;
  message: string;
  createdAt: string; // ISO 8601 format
}
```

### Örnek Kullanım

```javascript
const loadChatHistory = async (otherUserId, jwtToken) => {
  try {
    const response = await fetch(
      `http://localhost:8081/api/messages/${otherUserId}`,
      {
        headers: {
          'Authorization': `Bearer ${jwtToken}`
        }
      }
    );
    
    const messages = await response.json();
    return messages;
  } catch (error) {
    console.error('Chat geçmişi yüklenirken hata:', error);
    return [];
  }
};
```

---

## 🎣 Örnek React Hook

### `useChat.js`

```javascript
import { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from 'stompjs';

const useChat = (jwtToken) => {
  const [messages, setMessages] = useState([]);
  const [connected, setConnected] = useState(false);
  const stompClient = useRef(null);
  const currentUserId = useRef(null);

  useEffect(() => {
    if (!jwtToken) return;

    // JWT'den user ID'yi al
    try {
      const payload = JSON.parse(atob(jwtToken.split('.')[1]));
      currentUserId.current = payload.sub;
    } catch (error) {
      console.error('JWT parse hatası:', error);
      return;
    }

    // WebSocket bağlantısı kur
    const socket = new SockJS('http://localhost:8081/ws');
    stompClient.current = Stomp.over(socket);

    stompClient.current.connect(
      { 'Authorization': 'Bearer ' + jwtToken },
      () => {
        console.log('✅ Chat bağlantısı kuruldu');
        setConnected(true);

        // Mesajları dinle
        stompClient.current.subscribe('/user/queue/messages', (message) => {
          const chatMessage = JSON.parse(message.body);
          setMessages((prev) => [...prev, chatMessage]);
        });
      },
      (error) => {
        console.error('❌ Bağlantı hatası:', error);
        setConnected(false);
      }
    );

    // Cleanup
    return () => {
      if (stompClient.current) {
        stompClient.current.disconnect();
      }
    };
  }, [jwtToken]);

  // Mesaj gönder
  const sendMessage = (receiverId, messageText) => {
    if (!stompClient.current || !connected) {
      console.error('WebSocket bağlantısı yok!');
      return;
    }

    const messageDto = {
      receiverId: receiverId,
      message: messageText
    };

    stompClient.current.send('/app/chat', {}, JSON.stringify(messageDto));

    // Gönderilen mesajı hemen UI'da göster
    const sentMessage = {
      sender: { id: currentUserId.current },
      receiver: { id: receiverId },
      message: messageText,
      createdAt: new Date().toISOString()
    };
    setMessages((prev) => [...prev, sentMessage]);
  };

  // Chat geçmişini yükle
  const loadHistory = async (otherUserId) => {
    try {
      const response = await fetch(
        `http://localhost:8081/api/messages/${otherUserId}`,
        {
          headers: {
            'Authorization': `Bearer ${jwtToken}`
          }
        }
      );
      const history = await response.json();
      
      // DTO formatını entity formatına çevir (tutarlılık için)
      const formattedHistory = history.map(msg => ({
        id: msg.id,
        sender: { id: msg.senderId, fullName: msg.senderName },
        receiver: { id: msg.receiverId, fullName: msg.receiverName },
        message: msg.message,
        createdAt: msg.createdAt
      }));
      
      setMessages(formattedHistory);
    } catch (error) {
      console.error('Chat geçmişi yüklenirken hata:', error);
    }
  };

  return {
    messages,
    connected,
    currentUserId: currentUserId.current,
    sendMessage,
    loadHistory
  };
};

export default useChat;
```

### Kullanım Örneği

```javascript
import React, { useState } from 'react';
import useChat from './hooks/useChat';

const ChatComponent = ({ jwtToken }) => {
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [messageInput, setMessageInput] = useState('');
  const { messages, connected, currentUserId, sendMessage, loadHistory } = useChat(jwtToken);

  const handleSendMessage = () => {
    if (!messageInput.trim() || !selectedUserId) return;
    
    sendMessage(selectedUserId, messageInput);
    setMessageInput('');
  };

  const handleLoadHistory = () => {
    if (!selectedUserId) return;
    loadHistory(selectedUserId);
  };

  return (
    <div>
      <div>Status: {connected ? '🟢 Connected' : '🔴 Disconnected'}</div>
      <div>Your ID: {currentUserId}</div>
      
      <input
        type="number"
        placeholder="User ID to chat with"
        onChange={(e) => setSelectedUserId(e.target.value)}
      />
      <button onClick={handleLoadHistory}>Load History</button>

      <div style={{ height: '400px', overflow: 'auto' }}>
        {messages.map((msg, index) => (
          <div
            key={index}
            style={{
              textAlign: msg.sender.id == currentUserId ? 'right' : 'left'
            }}
          >
            <strong>{msg.sender.id == currentUserId ? 'You' : msg.sender.fullName}:</strong>
            <p>{msg.message}</p>
            <small>{new Date(msg.createdAt).toLocaleTimeString()}</small>
          </div>
        ))}
      </div>

      <input
        value={messageInput}
        onChange={(e) => setMessageInput(e.target.value)}
        placeholder="Type a message..."
        onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
      />
      <button onClick={handleSendMessage}>Send</button>
    </div>
  );
};

export default ChatComponent;
```

---

## ⚠️ Önemli Notlar

1. **JWT Token**: Her WebSocket bağlantısında ve API isteğinde `Authorization` header'ı gönderilmelidir.

2. **User ID**: JWT token'ın `sub` claim'inde user ID bulunur.

3. **Mesaj Formatları**:
   - **WebSocket mesajları**: Entity formatında (nested objeler)
   - **REST API response**: DTO formatında (flat yapı)

4. **Bağlantı Yönetimi**: Component unmount olduğunda WebSocket bağlantısını kapatmayı unutmayın.

5. **Error Handling**: Bağlantı kopması durumunda kullanıcıyı bilgilendirin ve yeniden bağlanma mekanizması ekleyin.

6. **CORS**: Production'da backend CORS ayarlarını düzenleyin.

---

## 🔐 Güvenlik

- JWT token'ları güvenli bir şekilde saklayın (localStorage veya httpOnly cookies)
- WebSocket bağlantısı her zaman JWT ile korunur
- User ID doğrulaması backend tarafında yapılır

---

## 📞 Test

Basit bir test için `http://localhost:8081/chat-test.html` adresini ziyaret edebilirsiniz.

---

## 🐛 Sorun Giderme

### Bağlantı Kurulamıyor
- Backend'in çalıştığından emin olun
- JWT token'ın geçerli olduğunu kontrol edin
- CORS ayarlarını kontrol edin

### Mesajlar Gelmiyor
- `/user/queue/messages` endpoint'ine subscribe edildiğinden emin olun
- WebSocket bağlantısının aktif olduğunu kontrol edin

### Chat Geçmişi Yüklenmiyor
- API endpoint'inin doğru olduğunu kontrol edin (`/api/messages/{userId}`)
- Authorization header'ının gönderildiğinden emin olun

---

İyi kodlamalar! 🚀
