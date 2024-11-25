import {Task} from "../../types/apiTypes.ts";

interface TaskProps {
    task: Task;
}

export default function TaskCard({task}: Readonly<TaskProps>) {
    return (
        <article>
            <h4>{task.title}</h4>
            <p>{task.description}</p>
            <p>Deadline: {task.deadline.valueOf()}</p>
        </article>
    );
};