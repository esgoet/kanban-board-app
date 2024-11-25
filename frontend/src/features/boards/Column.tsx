import TaskCard from "../tasks/TaskCard.tsx";
import {useMemo} from "react";
import {useGetTasksQuery} from "../api/apiSlice.ts";
import StatusHandler from "../api/StatusHandler.tsx";

type ColumnProps = {
    id: string,
    name: string
};

export default function Column({id, name}: Readonly<ColumnProps>) {
    const {
        data: tasks = [],
        isLoading,
        isSuccess,
        isError,
        error } = useGetTasksQuery();

    const columnTasks = useMemo(() => {
        return [...tasks].sort((a, b) => b.deadline.valueOf() - a.deadline.valueOf()).filter(task => task.columnId === id);
    }, [tasks]);

    return (
        <>
            <h3>{name}</h3>
            <StatusHandler isLoading={isLoading} isError={isError} isSuccess={isSuccess} error={error}>
                {columnTasks.map((task) => (
                <TaskCard key={task.id} task={task}/>
                ))}
            </StatusHandler>
        </>
    );
};