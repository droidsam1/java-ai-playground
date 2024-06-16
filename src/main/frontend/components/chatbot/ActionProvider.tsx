// in ActionProvider.jsx
import React from 'react';
import {nanoid} from "nanoid";

import {OpenAiAssistantService as AssistantService} from "Frontend/generated/endpoints";

const ActionProvider = ({ createChatBotMessage, setState, children }) => {

  const askOpenAi = (message) => {
    let first = true;
    const chatId = nanoid();
    AssistantService.chat(chatId, message)
        .onNext(token => {
          console.log(token);
            if (first && token) {
                addMessage(token);
                first = false;
            } else {
                appendToLatestMessage(token);
            }
        });
//         .onError(() => setWorking(false))
//         .onComplete(() => setWorking(false));
  };

  const addMessage= (message) => {
      createChatBotMessage(message);
   }

  const appendToLatestMessage = (botMessage) => {
        setState((prev) => ({
          ...prev,
          messages: [...prev.messages, botMessage],
        }));
      }

  return (
    <div>
      {React.Children.map(children, (child) => {
        return React.cloneElement(child, {
          actions: {
            askOpenAi,
          },
        });
      })}
    </div>
  );
};

export default ActionProvider;