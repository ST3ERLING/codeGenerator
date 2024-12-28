import React, { useState } from "react";
import axios from "axios";
import "../css/ChatSupport.css";
import agrandirIcon from "../assets/icons/agrandir.png"; // Import the icon

const ChatSupport = () => {
  const [messages, setMessages] = useState([
    { text: "Hello! How can I assist you?", type: "system" },
  ]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false); // To show loading while waiting for AI response
  const [isOpen, setIsOpen] = useState(false); // Track whether chat is open or closed
  const [isExpanded, setIsExpanded] = useState(false); // Track if chat is expanded

  const handleSend = async () => {
    if (input.trim()) {
      setMessages([...messages, { text: input, type: "user" }]);
      const userMessage = input;
      setInput("");
      setLoading(true);

      try {
        const response = await axios.post(
          "http://localhost:8888/prompt/api/chat",
          userMessage,
          {
            headers: { "Content-Type": "application/json" },
          }
        );

        setMessages((prevMessages) => [
          ...prevMessages,
          { text: response.data, type: "support" },
        ]);
      } catch (error) {
        console.error("Error communicating with the chat API:", error);
        setMessages((prevMessages) => [
          ...prevMessages,
          { text: "Error: Unable to connect to the AI", type: "system" },
        ]);
      } finally {
        setLoading(false);
      }
    }
  };

  const toggleChat = () => {
    setIsOpen((prev) => !prev);
  };

  const toggleExpand = () => {
    setIsExpanded((prev) => !prev); // Toggle expanded state
  };

  return (
    <div>
      {/* Button to open or close the chat */}
      <button className="chat-toggle-button" onClick={toggleChat}>
        Chat Support
      </button>

      {/* Only show chat if it's open */}
      {isOpen && (
        <div
          className={`chat-support-container ${
            isExpanded ? "chat-expanded" : ""
          }`}
        >
          <div className="chat-header" onClick={toggleChat}>
            Chat Support
          </div>
          <div className="chat-messages">
            {messages.map((msg, index) => (
              <div
                key={index}
                className={`chat-message ${
                  msg.type === "user" ? "user-message" : "support-message"
                }`}
              >
                {msg.text}
              </div>
            ))}
            {loading && <div className="chat-message support-message">Typing...</div>}
          </div>
          <div className="chat-input-container">
            <input
              type="text"
              className="chat-input"
              placeholder="Type a message..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyPress={(e) => e.key === "Enter" && handleSend()}
            />
            <button className="chat-send-button" onClick={handleSend}>
              Send
            </button>
            {/* Add the expand button */}
            <button className="chat-expand-button" onClick={toggleExpand}>
              <img
                src={agrandirIcon}
                alt="Expand"
                className="chat-expand-icon"
              />
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ChatSupport;
