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
      <div className="flex flex-col gap-m p-m box-border h-full" style={{ width: '100%' }}>
          <iframe src="https://www.macropay.com/" title="Macropay landing page" style={{ height: '100%'}}></iframe>
                      <Chatbot
                        config={config}
                        messageParser={MessageParser}
                        actionProvider={ActionProvider}
                      ></Chatbot>
      </div>
  );
}


