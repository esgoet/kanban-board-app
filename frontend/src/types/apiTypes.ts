export interface Board {
    id: string;
    name: string;
    columns: Column[];
}

export interface Column {
    id: string;
    name: string;
    tasks: string[];
}

export interface Task {
    id: string;
    columnId: string;
    title: string;
    description: string;
    deadline: Date;
}

export interface NewTask {
    columnId: string;
    title: string;
    description: string;
    deadline: Date;
}

export interface ApiResponse<T> {
    data: T;
    message: string;
    status: string;
}

export interface ApiError {
    message: string;
    timestamp: Date;
    statusCode: number;
}