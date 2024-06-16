import React, {useState, useEffect} from 'react';
import {OpenAiAssistantService as AssistantService} from "Frontend/generated/endpoints";
import {MessageInput} from "@vaadin/react-components/MessageInput";
import {nanoid} from "nanoid";
import MessageList from "Frontend/components/MessageList";
import {MessageItem} from "Frontend/components/Message";

const ChatBotPay = () => {
    const [chatId, setChatId] = useState(nanoid());
    const [working, setWorking] = useState(false);
    const [messages, setMessages] = useState<MessageItem[]>([{
        role: 'assistant',
        content: 'Welcome to Macropay! How can I help you?'
    }]);
   const [isMinimized, setIsMinimized] = useState(false); // New state for minimizing and maximizing the chatbot


    useEffect(() => {
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
        <div className="flex flex-col gap-m p-m box-border h-full" style={{
                                                                                              position: 'absolute',
                                                                                              bottom: '20px',
                                                                                              right: '20px',
                                                                                              width: isMinimized ? '100px' : '400px',
                                                                                              height: isMinimized ? '100px' : '400px',
                                                                                              backgroundColor: isMinimized? '': 'white',
                                                                                              borderRadius: '10px',
                                                                                              boxShadow: isMinimized? '0 0 0 0' : '0 0 10px rgba(0,0,0,0.25)'
                                                                                          }}>
            {isMinimized ? (
                <button className="rounded-lg" onClick={() => setIsMinimized(!isMinimized)} style={{ width: '100%', height: '100%', borderRadius: '50%',  backgroundColor: 'rgb(14 241 200)' , color: 'rgb(1 1 81/var(--tw-text-opacity))',  textAlign: 'center', fontWeight: '600'}}>
               MPay<br/> <div style={{ fontSize: '17px', fontWeight: '800', textShadow: '0 0 WHITE', animation: 'rainbow 5s ease-in-out infinite'}}>IA</div>
                </button>
            ) : (
                <>
            <button onClick={() => setIsMinimized(!isMinimized)} style={{ position: 'sticky', top: 0, '--tw-text-opacity': '1', color: 'rgb(1 1 81/var(--tw-text-opacity))' , backgroundColor: 'rgb(14 241 200)', textAlign: 'center', fontWeight:'600', fontSize: '15px' ,borderRadius:'10px', padding: '7px'}}>
              Macropay support
            </button>
            <MessageList messages={messages} className="flex-grow overflow-scroll"/>
            <MessageInput onSubmit={e => sendMessage(e.detail.value)} className="px-0"/>
                </>
            )}
        </div>
    );
};

export default ChatBotPay;