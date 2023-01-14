export class Item {
    id: string;
    name: string;
    type: ItemType;
    category: ItemCategory;
    currentAmount: number;
    maxAmount: number;
    value: number;
    valueBool: boolean
}

export class ItemToAdd {
    id: string;
    name: string;
    type: string;
    category: string;
    currentAmount: number;
    maxAmount: number;
}

export enum ItemType {
    AMOUNT = "item.amount",
    WEIGHT = "item.weight",
    UNLIMITED = "item.unlimited"
}

export enum ItemCategory {
    CLOTHES = "category.clothes",
    FOOD = "category.food",
    ANIMALS = "category.animals",
    CHILDREN = "category.children",
    MEDICINE = "category.medicine",
    ELECTRONIC = "category.electronic",
    OTHER = "category.other",
}
