import React, { useState } from 'react';
import axios from 'axios';
import '../css/ChatSupport.css';

const ChatSupport = () => {
  const [messages, setMessages] = useState([
    { text: 'Hello! How can I assist you', type: 'system' },
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false); // To show loading while waiting for AI response
  const [isOpen, setIsOpen] = useState(false); // Track whether chat is open or closed

  const handleSend = async () => {
    if (input.trim()) {
      // Add user's message to the chat
      setMessages([...messages, { text: input, type: 'user' }]);
      const userMessage = input;
      setInput('');
      setLoading(true);

      try {
        // Send the user's message to the backend API
        const response = await axios.post('http://localhost:8888/prompt/api/chat', userMessage, {
          headers: { 'Content-Type': 'application/json' },
        });

        // Add the AI's response to the chat
        setMessages((prevMessages) => [
          ...prevMessages,
          { text: response.data, type: 'support' },
        ]);
      } catch (error) {
        console.error('Error communicating with the chat API:', error);
        setMessages((prevMessages) => [
          ...prevMessages,
          { text: 'Error: Unable to connect to the AI', type: 'system' },
        ]);
      } finally {
        setLoading(false);
      }
    }
  };

  const toggleChat = () => {
    setIsOpen((prev) => !prev); // Toggle chat visibility
  };

  return (
    <div>
      {/* Button to open or close the chat */}
      <button className="chat-toggle-button" onClick={toggleChat}>
      Chat Support
      </button>

      {/* Only show chat if it's open */}
      {isOpen && (
        <div className="chat-support-container">
          <div className="chat-header" onClick={toggleChat} style={{ cursor: 'pointer' }}>Chat Support</div>
          <div className="chat-messages">
            {messages.map((msg, index) => (
              <div
                key={index}
                className={`chat-message ${msg.type === 'user' ? 'user-message' : 'support-message'}`}
              >
                {msg.text}
              </div>
            ))}
            {loading && (
              <div className="chat-message support-message">Typing...</div>
            )}
          </div>
          <div className="chat-input-container">
            <input
              type="text"
              className="chat-input"
              placeholder="Type a message..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSend()}
            />
            <button className="chat-send-button" onClick={handleSend}>
              Send
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ChatSupport;
