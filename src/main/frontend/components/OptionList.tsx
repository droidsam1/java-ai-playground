import React from 'react';
import { Button } from '@vaadin/react-components';

export interface Option {
  id: number;
  label: string;
  action: string | null;
  message: string | null;
}

export class OrderOption implements Option {
  id: number = 1;
  label: string = "I need help to create an order";
  action: string = "Describe step by step how to create an order using CURL based on the documentation provided.";
  message: string | null = null;
}

export class PaymentOption implements Option {
  id: number = 2;
  label: string = "I need help to create a payment";
  action: string = "Describe step by step how to create a payment using CURL based on the documentation provided.";
  message: string | null = null;
}

export class SubscriptionOption implements Option {
  id: number = 3;
  label: string = "I need help to create a subscription";
  action: string = "Describe step by step how to create a subscription using CURL based on the documentation provided.";
  message: string | null = null;
}

export class OtherOption implements Option {
  id: number = 4;
  label: string = "Other";
  action: string | null = null;
  message: string | null = "Please describe your concern";
}

interface OptionsListProps {
  onOptionSelect: (option: Option) => void;
}

const OptionsComponent: React.FC<OptionsListProps> = ({ onOptionSelect }) => {
  const options: Option[] = [
    new OrderOption(),
    new PaymentOption(),
    new SubscriptionOption(),
    new OtherOption(),
  ];

  return (
      <div>
        {options.map(option => (
            <Button key={option.id} onClick={() => onOptionSelect(option)}>
              {option.label}
            </Button>
        ))}
      </div>
  );
}

export default OptionsComponent;
