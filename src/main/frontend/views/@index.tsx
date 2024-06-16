import {useEffect, useState} from "react";
import {OpenAiAssistantService as AssistantService} from "Frontend/generated/endpoints";
import {MessageInput} from "@vaadin/react-components/MessageInput";
import {nanoid} from "nanoid";
import {SplitLayout} from "@vaadin/react-components/SplitLayout";
import Message, {MessageItem} from "../components/Message";
import MessageList from "Frontend/components/MessageList";

// import config from 'Frontend/components/chatbot/config.js';
import Chatbot from 'react-chatbot-kit'
import 'react-chatbot-kit/build/main.css';
import config from "Frontend/components/chatbot/config";
import MessageParser from 'Frontend/components/chatbot/MessageParser';
import ActionProvider from 'Frontend/components/chatbot/ActionProvider';

import ChatBot from 'Frontend/components/bot/ChatBotPay';


export default function Index() {
    const [chatId, setChatId] = useState(nanoid());
    const [working, setWorking] = useState(false);
    const [messages, setMessages] = useState<MessageItem[]>([{
        role: 'assistant',
        content: 'Welcome to Macropay! How can I help you?'
    }]);

    useEffect(() => {
        // Here we can update data or whatever
        console.log("Use effect to refresh info")
    }, [working]);

    function addMessage(message: MessageItem) {
        setMessages(messages => [...messages, message]);
    }

    function appendToLatestMessage(chunk: string) {
        setMessages(messages => {
            const latestMessage = messages[messages.length - 1];
            latestMessage.content += chunk;
            return [...messages.slice(0, -1), latestMessage];
        });
    }

    async function sendMessage(message: string) {
        setWorking(true);
        addMessage({
            role: 'user',
            content: message
        });
        let first = true;
        AssistantService.chat(chatId, message)
            .onNext(token => {
                if (first && token) {
                    addMessage({
                        role: 'assistant',
                        content: token
                    });

                    first = false;
                } else {
                    appendToLatestMessage(token);
                }
            })
            .onError(() => setWorking(false))
            .onComplete(() => setWorking(false));
    }


  return (
    <SplitLayout className="h-full">
       <div style={{ position: 'relative', height: '100vh', width: '100vw' }}>
            <iframe
                src="https://www.macropay.com/"
                title="Macropay landing page"
                style={{ position: 'absolute', top: 0, left: 0, height: '100%', width: '100%', border: 0 }}
            ></iframe>
        </div>
        <ChatBot        />

    </SplitLayout>
  );
}


