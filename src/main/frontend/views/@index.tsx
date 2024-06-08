import {useEffect, useState} from "react";
import {AssistantService} from "Frontend/generated/endpoints";
import {MessageInput} from "@vaadin/react-components/MessageInput";
import {nanoid} from "nanoid";
import {SplitLayout} from "@vaadin/react-components/SplitLayout";
import Message, {MessageItem} from "../components/Message";
import MessageList from "Frontend/components/MessageList";
import OptionList from "Frontend/components/OptionList";

interface Option {
    id: number;
    label: string;
}

export default function Index() {
    const [selectedOption, setSelectedOption] = useState<Option | null>(null);
    const [chatId, setChatId] = useState(nanoid());
    const [working, setWorking] = useState(false);
    const [messages, setMessages] = useState<MessageItem[]>([{
        role: 'assistant',
        content: 'Welcome to Macropay! How can I help you?'
    }]);

    const handleOptionSelect = (option: Option) => {
        setSelectedOption(option);
        console.log("Opción seleccionada:", option);
    };

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
      <div className="flex flex-col gap-m p-m box-border h-full" style={{ width: '75%' }}>
          <iframe src="https://www.macropay.com/" title="Macropay landing page" style={{ height: '100%'}}></iframe>
      </div>
      <div className="flex flex-col gap-m p-m box-border h-full" style={{width: '25%'}}>
        <h3>Macropay support</h3>
          <MessageList
              className="flex-grow overflow-scroll"
              messages={messages}
          />
          <OptionList
              onOptionSelect={handleOptionSelect}
          />
          <MessageInput
              className="px-0"
              disabled={selectedOption === null}
              onSubmit={e => sendMessage(e.detail.value)}
          />
      </div>

    </SplitLayout>
  );
}
